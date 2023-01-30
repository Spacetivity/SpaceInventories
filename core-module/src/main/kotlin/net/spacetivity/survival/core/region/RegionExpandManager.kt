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
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.util.Vector

class RegionExpandManager : Listener {

    val chunkManager: ChunkManager = SpaceSurvivalPlugin.instance.chunkManager
    val regionManager: RegionManager = SpaceSurvivalPlugin.instance.regionManager

    val selectorPositionYModifier: Double = 10.0
    val selectorEntityYModifier: Double = 5.0
    val selectorEntityType: EntityType = EntityType.SHULKER

    init {
        Bukkit.getPluginManager().registerEvents(this, SpaceSurvivalPlugin.instance)
    }

    fun initExpandProcess(player: Player, region: ClaimedRegion): ExpandResult {
        if (player.hasMetadata("chunkExpandSessionOwner")) return ExpandResult.ALREADY_IN_CLAIMING_PROCESS
        if (region.hasReachedClaimingLimit()) return ExpandResult.REACHED_MAX_CLAIM_LIMIT

        val originalChunk: Chunk = player.chunk
        val highestPointInChunk: Location = chunkManager.getHighestPointInChunk(originalChunk)
        val selectionBoxMiddleLocation: Location =
            chunkManager.getChunkCenterLocation(highestPointInChunk.y, originalChunk)

        selectionBoxMiddleLocation.block.type = Material.GLASS //TODO: Material.BARRIER

        spawnSelectionEntities(player, highestPointInChunk.y + selectorEntityYModifier)

        player.setMetadata("chunkExpandSessionOwner", FixedMetadataValue(SpaceSurvivalPlugin.instance, 1))
        player.teleport(selectionBoxMiddleLocation.clone().add(0.0, selectorPositionYModifier, 0.0))
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
            shulker.setMetadata(
                "chunkExpansionProcess_${player.uniqueId}", FixedMetadataValue(
                    SpaceSurvivalPlugin.instance,
                    Pair(chunk.x, chunk.z)
                )
            )

            shulker.isCustomNameVisible = true
            shulker.customName(
                Component.text(
                    "Chunk: ${chunk.x}, ${chunk.z}",
                    NamedTextColor.GOLD,
                    TextDecoration.BOLD
                )
            )

        }
    }

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        val player = event.player

        if (!player.hasMetadata("chunkExpandSessionOwner")) return

        val selectorEntities: MutableList<Shulker> =
            player.world.entities.filter { entity: Entity? -> entity != null && entity.type == EntityType.SHULKER }
                .map { entity: Entity -> entity as Shulker }.toMutableList()

        for (selectorEntity: Shulker in selectorEntities) {
            val playerEyeLocation: Location = player.eyeLocation
            val vector: Vector = selectorEntity.eyeLocation.toVector().subtract(playerEyeLocation.toVector())
            val dotProduct: Double = vector.normalize().dot(playerEyeLocation.direction)
            handleEntityStatus(dotProduct > 0.99, selectorEntity)
        }
    }

    fun handleEntityStatus(isActive: Boolean, entity: Shulker) {
        entity.isGlowing = isActive
        //TODO: handle name update
    }

}