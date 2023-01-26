package net.spacetivity.survival.core.listener

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.spacetivity.survival.core.SpaceSurvivalPlugin
import net.spacetivity.survival.core.chunk.ChunkPlayer
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class TestListener(private var plugin: SpaceSurvivalPlugin) : Listener {

    @EventHandler
    fun onBreak(event: BlockBreakEvent) {
        val player = event.player
        val chunkManager = plugin.chunkManager

        val chunksAroundChunk: MutableList<Chunk> = chunkManager.getChunksAroundChunk(player.chunk, true)

        chunksAroundChunk.forEach { chunk: Chunk ->
            val center: Location = chunkManager.getChunkCenterLocation(player.location.y + 5, chunk)
            center.block.type = Material.EMERALD_BLOCK

            val spawnEntity: LivingEntity = center.world.spawnEntity(center, EntityType.VILLAGER) as LivingEntity
            spawnEntity.setAI(false)
            spawnEntity.setGravity(false)
            player.sendMessage(Component.text("A selection entity has appeared!").color(NamedTextColor.LIGHT_PURPLE))
        }

    }

    @EventHandler
    fun onClaim(event: BlockPlaceEvent) {
        val player = event.player

        if (!player.isOp) return
        if (event.block.type != Material.GOLD_BLOCK) return

        val chunk = player.chunk

        plugin.playerChunkManager.getPlayer(player.uniqueId).let { chunkPlayer: ChunkPlayer? ->
            val result = chunkPlayer?.claimChunk(chunk)
            player.sendMessage(Component.text("Claim status ${result?.name}"))
        }
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        val chunkPlayer = ChunkPlayer(plugin, player.uniqueId, mutableListOf())
        plugin.playerChunkManager.registerPlayer(chunkPlayer)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        plugin.playerChunkManager.unregisterPlayer(player.uniqueId)
    }
}