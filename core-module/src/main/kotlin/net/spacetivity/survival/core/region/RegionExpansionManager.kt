package net.spacetivity.survival.core.region

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.spacetivity.survival.core.SpaceSurvivalPlugin
import net.spacetivity.survival.core.chunk.ChunkManager
import net.spacetivity.survival.core.chunk.ClaimResult
import net.spacetivity.survival.core.utils.ItemBuilder
import org.bukkit.*
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Shulker
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
    val regionManager: RegionManager = SpaceSurvivalPlugin.instance.regionManager

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

        spawnSelectionEntities(player, highestPointInChunk.y + selectorEntityYModifier)

        player.setMetadata("chunkExpandSessionOwner", FixedMetadataValue(SpaceSurvivalPlugin.instance, 1))
        player.teleport(selectionBoxMiddleLocation.add(0.0, 1.0, 0.0))

        player.sendMessage(
            Component.text(
                "Now look at the spawned entity above your neighbour chunks to claim it.",
                NamedTextColor.YELLOW
            )
        )

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

    fun spawnSelectionEntities(player: Player, yLevel: Double) {
        val originalChunk: Chunk = player.chunk
        val world: World = player.world
        val chunksAroundChunk: MutableList<Chunk> = chunkManager.getChunksAroundChunk(originalChunk, false)

        chunksAroundChunk.forEach { chunk: Chunk ->
            val centerLocationOfUnclaimedChunk: Location = chunkManager.getChunkCenterLocation(yLevel, chunk)
            val shulker: Shulker = world.spawnEntity(centerLocationOfUnclaimedChunk, selectorEntityType) as Shulker

            shulker.setAI(false)
            shulker.setGravity(false)
            shulker.setMetadata("expansionEntity", FixedMetadataValue(SpaceSurvivalPlugin.instance, 1))
            shulker.setMetadata(
                "chunkExpansionProcess_${player.uniqueId}", FixedMetadataValue(
                    SpaceSurvivalPlugin.instance,
                    Pair(chunk.x, chunk.z)
                )
            )

            shulker.isCustomNameVisible = true
            shulker.customName(getEntityName(shulker.chunk, false))

        }
    }

    fun handleInteractionChecking() {
        expandTask = Bukkit.getScheduler().runTaskTimer(SpaceSurvivalPlugin.instance, Runnable {
            Bukkit.getOnlinePlayers().filter { player -> player.hasMetadata("chunkExpandSessionOwner") }
                .forEach { player: Player ->

                    val selectorEntities: MutableList<Shulker> =
                        player.world.entities.filter { entity: Entity? ->
                            entity != null && entity.type == selectorEntityType && entity.hasMetadata(
                                "chunkExpansionProcess_${player.uniqueId}"
                            )
                        }.map { entity: Entity -> entity as Shulker }.toMutableList()

                    for (entity: Shulker in selectorEntities) {
                        val playerEyeLocation: Location = player.eyeLocation
                        val vector: Vector = entity.eyeLocation.toVector().subtract(playerEyeLocation.toVector())
                        val dotProduct: Double = vector.normalize().dot(playerEyeLocation.direction)
                        val isActive: Boolean = dotProduct > 0.99

                        if (isActive) {
                            if (!player.hasMetadata("currentChunk"))
                                player.setMetadata(
                                    "currentChunk",
                                    FixedMetadataValue(
                                        SpaceSurvivalPlugin.instance,
                                        Pair(entity.chunk.x, entity.chunk.z)
                                    )
                                )

                            if (player.hasMetadata("currentChunk") && player.getMetadata("currentChunk")[0].value() != Pair(
                                    entity.chunk.x,
                                    entity.chunk.z
                                )
                            ) {
                                player.removeMetadata("currentChunk", SpaceSurvivalPlugin.instance)
                                player.setMetadata(
                                    "currentChunk",
                                    FixedMetadataValue(
                                        SpaceSurvivalPlugin.instance,
                                        Pair(entity.chunk.x, entity.chunk.z)
                                    )
                                )
                            }

                            showChunkOutline(entity.chunk, entity.location.blockY, player)
                        }

                        handleEntityStatus(entity.chunk, isActive, entity)
                    }

                }
        }, 0L, 20L)
    }

    fun showChunkOutline(chunk: Chunk, yLevel: Int, player: Player) {
        val minX: Int = chunk.x * 16
        val minZ: Int = chunk.z * 16

        val dustOptions: Particle.DustOptions = Particle.DustOptions(
            Color.fromBGR(232, 183, 35),
            1F
        )

        for (x in minX until minX + 17) for (y in yLevel until yLevel + 1) for (z in minZ until minZ + 17) {
            player.spawnParticle(Particle.REDSTONE, minX.toDouble(), y.toDouble(), z.toDouble(), 20, dustOptions)
            player.spawnParticle(Particle.REDSTONE, x.toDouble(), y.toDouble(), minZ.toDouble(), 20, dustOptions)
            player.spawnParticle(Particle.REDSTONE, minX + 17.0, y.toDouble(), z.toDouble(), 20, dustOptions)
            player.spawnParticle(Particle.REDSTONE, x.toDouble(), y.toDouble(), minZ + 17.0, 20, dustOptions)
        }
    }

    fun handleEntityStatus(chunk: Chunk, isActive: Boolean, entity: Shulker) {
        entity.isGlowing = isActive
        entity.customName(getEntityName(chunk, isActive))
    }

    fun getEntityName(chunk: Chunk, isActive: Boolean): Component {
        return Component.text(
            "Chunk: ${chunk.x}, ${chunk.z}",
            if (isActive) NamedTextColor.GREEN else NamedTextColor.GOLD,
            TextDecoration.BOLD
        )
    }

    fun getPlayersInExpansionProcess(): List<Player> {
        return Bukkit.getOnlinePlayers().filter { player -> player.hasMetadata("chunkExpandSessionOwner") }
    }

    fun cancelExpansion(player: Player) {
        val originalChunk: Chunk = player.chunk
        val highestPointInChunk: Location = chunkManager.getHighestPointInChunk(originalChunk).clone()
        val selectionBoxMiddleLocation: Location =
            chunkManager.getChunkCenterLocation(highestPointInChunk.y, originalChunk).clone()

        selectionBoxMiddleLocation.block.type = Material.AIR

        player.inventory.clear()
        SpaceSurvivalPlugin.instance.inventoryManager.loadInventory(player)

        player.removeMetadata("chunkExpandSessionOwner", SpaceSurvivalPlugin.instance)
        player.removeMetadata("currentChunk", SpaceSurvivalPlugin.instance)

        player.world.entities.filter { entity -> entity.hasMetadata("chunkExpansionProcess_${player.uniqueId}") }
            .forEach { e -> e.remove() }
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