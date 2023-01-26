package net.spacetivity.survival.core.listener

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.spacetivity.survival.core.SpaceSurvivalPlugin
import net.spacetivity.survival.core.chunk.container.ChunkPlayer
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

        val chunk = event.block.chunk

        plugin.playerChunkManager.getPlayer(player.uniqueId).let { chunkPlayer: ChunkPlayer? ->
            val result = chunkPlayer?.claimChunk(chunk)
            val color: TextColor = if (result?.isSuccess == true) TextColor.color(155, 252, 98) else NamedTextColor.RED
            player.sendActionBar(Component.text("Claim status ${result?.name} | Owner is: ${plugin.chunkManager.getChunkOwner(chunk)}").color(color))
        }
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        val chunkPlayer = ChunkPlayer(plugin, player.uniqueId, mutableListOf())
        plugin.playerChunkManager.registerPlayer(chunkPlayer)

        player.sendMessage(Component.text("Coordinates of all your claimed chunks:"))
        plugin.chunkManager.getClaimedChunksByPlayer(player.uniqueId).forEach { pair ->
            plugin.chunkManager.registerChunk(player.uniqueId, pair.first, pair.second)
            player.sendMessage(Component.text("Chunk coords - X: ${pair.first} Z: ${pair.second}"))
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        plugin.playerChunkManager.unregisterPlayer(player.uniqueId)
        plugin.chunkManager.clearRegisteredChunks(player.uniqueId)
    }
}