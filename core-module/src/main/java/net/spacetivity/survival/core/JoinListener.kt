package net.spacetivity.survival.core

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.spacetivity.survival.core.chunk.ChunkPlayer
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLevelChangeEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.metadata.FixedMetadataValue

class JoinListener(private var plugin: SpaceSurvivalPlugin) : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        val chunkPlayer = ChunkPlayer(plugin, player.uniqueId, mutableListOf())
        chunkPlayer.save()
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        plugin.playerChunkManager.getPlayer(player.uniqueId).let { chunkPlayer: ChunkPlayer? -> chunkPlayer?.delete() }
    }

    @EventHandler
    fun onChunkChange(event: PlayerMoveEvent) {
        val player = event.player

        if (!player.hasMetadata("chunkX") && !player.hasMetadata("chunkZ")) {
            player.setMetadata("chunkX", FixedMetadataValue(plugin, player.chunk.x))
            player.setMetadata("chunkZ", FixedMetadataValue(plugin, player.chunk.z))
            player.sendMessage(Component.text("Entered new chunk!").color(NamedTextColor.DARK_GREEN))
            return
        }

        if (player.chunk.x != player.getMetadata("chunkX")[0].value() || player.chunk.z != player.getMetadata("chunkZ")[0].value()) {
            player.removeMetadata("chunkX", plugin)
            player.setMetadata("chunkX", FixedMetadataValue(plugin, player.chunk.x))
            player.removeMetadata("chunkZ", plugin)
            player.setMetadata("chunkZ", FixedMetadataValue(plugin, player.chunk.z))
            player.sendMessage(Component.text("Entered new chunk!").color(NamedTextColor.DARK_GREEN))
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