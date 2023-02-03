package net.spacetivity.survival.core.region

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.spacetivity.survival.core.SpaceSurvivalPlugin
import net.spacetivity.survival.core.chunk.ChunkManager
import net.spacetivity.survival.core.chunk.ClaimResult
import net.spacetivity.survival.core.region.entity.RegionSelector
import net.spacetivity.survival.core.region.entity.RegionSelectorManager
import net.spacetivity.survival.core.utils.ItemBuilder
import org.bukkit.*
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.Vector

@Suppress("UNCHECKED_CAST")
class RegionExpansionManager : Listener {

    val chunkManager: ChunkManager = SpaceSurvivalPlugin.instance.chunkManager
    val selectorManager: RegionSelectorManager = SpaceSurvivalPlugin.instance.regionSelectorManager

    val selectorPositionYModifier: Double = 20.0
    val selectorEntityYModifier: Double = 10.0
    val selectorEntityType: EntityType = EntityType.SHULKER

    lateinit var expandTask: BukkitTask

    init {
        Bukkit.getPluginManager().registerEvents(this, SpaceSurvivalPlugin.instance)
    }

    fun initExpandProcess(player: Player, region: ClaimedRegion): ExpandResult {
        if (player.hasMetadata("chunkExpandSessionOwner")) return ExpandResult.ALREADY_IN_CLAIMING_PROCESS
        if (region.hasReachedClaimingLimit()) return ExpandResult.REACHED_MAX_CLAIM_LIMIT

        val originalChunk: Chunk = player.chunk
        val highestPointInChunk: Location = chunkManager.getHighestPointInChunk(originalChunk).clone()
        val selectionBoxMiddleLocation: Location =
            chunkManager.getChunkCenterLocation(highestPointInChunk.y + selectorPositionYModifier, originalChunk)
                .clone()

        selectionBoxMiddleLocation.block.type = Material.GLASS //TODO: Material.BARRIER

        initSelectors(player, highestPointInChunk.y + selectorEntityYModifier)

        player.setMetadata("chunkExpandSessionOwner", FixedMetadataValue(SpaceSurvivalPlugin.instance, 1))
        player.teleport(selectionBoxMiddleLocation.add(0.0, 1.0, 0.0))

        SpaceSurvivalPlugin.instance.inventoryManager.saveInventory(player)
        player.inventory.clear()

        player.inventory.setItem(
            1, ItemBuilder(Material.LIME_DYE)
                .setName(Component.text("CLAIM CHUNK", NamedTextColor.GREEN, TextDecoration.BOLD))
                .onInteract {
                    val coords: Pair<Int, Int> = player.getMetadata("currentChunk")[0].value() as Pair<Int, Int>
                    val chunk: Chunk = player.world.getChunkAt(coords.first, coords.second)
                    val result: ClaimResult = chunkManager.claimChunk(player.uniqueId, chunk, true)

                    player.sendMessage(
                        Component.text(
                            if (result.isSuccess) "Successfully claimed chunk (${chunk.x} | ${chunk.z})" else "Error: ${result.name}",
                            if (result.isSuccess) NamedTextColor.GREEN else NamedTextColor.RED
                        )
                    )

                    cancelExpansion(player) //TODO: change that to allow multi chunk claiming

                }
                .build()
        )

        player.inventory.setItem(7, ItemBuilder(Material.BARRIER)
            .setName(Component.text("CANCEL", NamedTextColor.RED, TextDecoration.BOLD))
            .onInteract { cancelExpansion(player) }
            .build())

        return ExpandResult.SUCCESS
    }

    fun initSelectors(player: Player, yLevel: Double) {
        for (chunk in chunkManager.getChunksAroundChunk(player.chunk, false)) {
            val selector = RegionSelector(player, chunk, false)
            selector.spawn(yLevel)
        }
    }

    fun handleInteractionChecking() {
        expandTask = Bukkit.getScheduler().runTaskTimer(SpaceSurvivalPlugin.instance, Runnable {
            Bukkit.getOnlinePlayers().filter { player -> player.hasMetadata("chunkExpandSessionOwner") }
                .forEach { player: Player ->
                    for (selector in selectorManager.getSelectorsFromPlayer(player)) {
                        val eyeLocation: Location = player.eyeLocation
                        val vector: Vector = selector.entity.eyeLocation.toVector().subtract(eyeLocation.toVector())
                        val dotProduct: Double = vector.normalize().dot(eyeLocation.direction)
                        val isActive: Boolean = dotProduct > 0.99

                        selector.update(isActive, selector.entity.location.blockY)
                    }
                }
        }, 0L, 20L)
    }

    fun cancelExpansion(player: Player) {
        chunkManager.getChunkCenterLocation(chunkManager.getHighestPointInChunk(player.chunk).clone().y, player.chunk)
            .clone().block.type = Material.AIR

        player.removeMetadata("chunkExpandSessionOwner", SpaceSurvivalPlugin.instance)
        SpaceSurvivalPlugin.instance.regionSelectorManager.removeSelectorsFromPlayer(player)
        SpaceSurvivalPlugin.instance.inventoryManager.loadInventory(player)
    }

    @EventHandler
    fun onSelectorEntityDamageByPlayer(event: EntityDamageByEntityEvent) {
        if (event.cause != DamageCause.ENTITY_ATTACK) return
        if (!event.entity.hasMetadata("expansionEntity")) return

        event.isCancelled = true
        event.damage = 0.0
    }

    @EventHandler
    fun onDrop(event: PlayerDropItemEvent) {
        val player = event.player

        if (!player.hasMetadata("chunkExpandSessionOwner")) return

        event.isCancelled = true
    }

    @EventHandler
    fun onItemMove(event: InventoryClickEvent) {
        val player = event.whoClicked as Player

        if (!player.hasMetadata("chunkExpandSessionOwner")) return

        event.isCancelled = true
    }
}