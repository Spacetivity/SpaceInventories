package net.spacetivity.survival.core.listener

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.spacetivity.survival.core.SpaceSurvivalPlugin
import net.spacetivity.survival.core.chunk.ChunkPlayer
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLevelChangeEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.metadata.FixedMetadataValue

class TestListener(private var plugin: SpaceSurvivalPlugin) : Listener {

    @EventHandler
    fun onClaim(event: BlockPlaceEvent) {
        if (event.block.type != Material.GOLD_BLOCK) return
        val player = event.player
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

    @EventHandler
    fun onChunkChange(event: PlayerMoveEvent) {
        val player = event.player

        if (!player.hasMetadata("chunkX") && !player.hasMetadata("chunkZ")) {
            player.setMetadata("chunkX", FixedMetadataValue(plugin, player.chunk.x))
            player.setMetadata("chunkZ", FixedMetadataValue(plugin, player.chunk.z))
            player.sendMessage(Component.text("Entered new chunk!").color(NamedTextColor.DARK_GREEN))
            val chunkClaimed = plugin.chunkManager.isChunkClaimed(player.chunk)
            player.sendMessage(Component.text("Chunk claimed: $chunkClaimed").color(NamedTextColor.YELLOW))
            return
        }

        if (player.chunk.x != player.getMetadata("chunkX")[0].value() || player.chunk.z != player.getMetadata("chunkZ")[0].value()) {
            player.removeMetadata("chunkX", plugin)
            player.setMetadata("chunkX", FixedMetadataValue(plugin, player.chunk.x))
            player.removeMetadata("chunkZ", plugin)
            player.setMetadata("chunkZ", FixedMetadataValue(plugin, player.chunk.z))
            player.sendMessage(Component.text("Entered new chunk!").color(NamedTextColor.DARK_GREEN))
            val chunkClaimed = plugin.chunkManager.isChunkClaimed(player.chunk)
            player.sendMessage(Component.text("Chunk claimed: $chunkClaimed").color(NamedTextColor.YELLOW))
        }
    }

    @EventHandler
    fun move(event: PlayerLevelChangeEvent) {
        val player = event.player

        val location = Location(
            player.world,
            player.location.chunk.x.toDouble() * 16,
            player.location.y,
            player.location.chunk.z.toDouble() * 16
        )

        location.block.setType(Material.GOLD_BLOCK, true)
        println("PLACED A BLOCK IN CHUNK OF PLAYER ${player.name}")
    }
}