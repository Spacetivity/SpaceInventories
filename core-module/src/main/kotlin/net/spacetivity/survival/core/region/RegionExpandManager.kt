package net.spacetivity.survival.core.region

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.spacetivity.survival.core.SpaceSurvivalPlugin
import net.spacetivity.survival.core.chunk.ChunkManager
import org.bukkit.*
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Shulker
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.Vector

class RegionExpandManager : Listener {

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
        val highestPointInChunk: Location = chunkManager.getHighestPointInChunk(originalChunk)
        val selectionBoxMiddleLocation: Location =
            chunkManager.getChunkCenterLocation(highestPointInChunk.y + selectorPositionYModifier, originalChunk)

        selectionBoxMiddleLocation.block.type = Material.GLASS //TODO: Material.BARRIER

        spawnSelectionEntities(player, highestPointInChunk.y + selectorEntityYModifier)

        player.setMetadata("chunkExpandSessionOwner", FixedMetadataValue(SpaceSurvivalPlugin.instance, 1))
        player.teleport(selectionBoxMiddleLocation.clone().add(0.0, 1.0, 0.0))
        player.sendMessage("Now look at the spawned entity above your neighbour chunks to claim it.")

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

                    for (selectorEntity: Shulker in selectorEntities) {
                        val playerEyeLocation: Location = player.eyeLocation
                        val vector: Vector =
                            selectorEntity.eyeLocation.toVector().subtract(playerEyeLocation.toVector())
                        val dotProduct: Double = vector.normalize().dot(playerEyeLocation.direction)
                        val isActive: Boolean = dotProduct > 0.99

                        if (isActive) showChunkOutline(selectorEntity.chunk, selectorEntity.location.blockY, player)
                        handleEntityStatus(selectorEntity.chunk, isActive, selectorEntity)
                    }

                }
        }, 0L, 20L)
    }

    fun showChunkOutline(chunk: Chunk, yLevel: Int, player: Player) {
        val minX = chunk.x * 16
        val minZ = chunk.z * 16

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

    @EventHandler
    fun onSelectorEntityDamageByPlayer(event: EntityDamageByEntityEvent) {
        if (event.cause != DamageCause.ENTITY_ATTACK) return
        if (!event.entity.hasMetadata("expansionEntity")) return

        event.isCancelled = true
        event.damage = 0.0
    }
}