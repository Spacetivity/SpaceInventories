package net.spacetivity.survival.core.listener

import net.spacetivity.survival.core.SpaceSurvivalPlugin
import net.spacetivity.survival.core.translation.TranslationKey
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class TestListener(private var plugin: SpaceSurvivalPlugin) : Listener {

    @EventHandler
    fun onBreak(event: BlockBreakEvent) {
       /* val player = event.player
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
        */
    }

    @EventHandler
    fun onClaim(event: BlockPlaceEvent) {
        val player = event.player

        if (!player.isOp) return
        if (event.block.type != Material.GOLD_BLOCK) return

        plugin.regionManager.initRegion(player)

        plugin.translator.sendMessage(player, TranslationKey.JOIN_MESSAGE)

        /* val result = plugin.chunkManager.claimChunk(player.uniqueId, chunk, true)
        val color: TextColor = if (result.isSuccess) NamedTextColor.GREEN else NamedTextColor.RED

        player.sendActionBar(Component.text("Claim status ${result.name} | Owner is: ${plugin.chunkManager.getChunkOwner(chunk)
            ?.let { Bukkit.getOfflinePlayer(it) }}").color(color)) */
    }

    fun test(vararg names: String) {

    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        plugin.chunkManager.loadClaimedChunks(player.uniqueId)
        plugin.regionManager.loadRegion(player.uniqueId)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        plugin.chunkManager.unregisterRegisteredChunks(player.uniqueId)
        plugin.regionManager.unregisterRegion(player.uniqueId)
    }
}