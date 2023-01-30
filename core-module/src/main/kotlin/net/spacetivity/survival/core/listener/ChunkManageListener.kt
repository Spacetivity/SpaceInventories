package net.spacetivity.survival.core.listener

import net.spacetivity.survival.core.SpaceSurvivalPlugin
import net.spacetivity.survival.core.translation.TranslationKey
import net.spacetivity.survival.core.translation.Translator
import net.spacetivity.survival.core.utils.ItemBuilder
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemFlag

class ChunkManageListener(private var plugin: SpaceSurvivalPlugin) : Listener {

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
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        plugin.chunkManager.loadClaimedChunks(player.uniqueId)
        plugin.regionManager.loadRegion(player.uniqueId)

        player.inventory.setItem(4, ItemBuilder(Material.SCULK_CATALYST)
                .setName(Translator.getTranslation(TranslationKey.CLAIM_ITEM_NAME))
                .addEnchantment(Enchantment.DURABILITY, 1)
                .setLoreByString(mutableListOf("Place this block to claim your", "first chunk in the survival world."))
                .addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                .build()
        )

    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        plugin.chunkManager.unregisterRegisteredChunks(player.uniqueId)
        plugin.regionManager.unregisterRegion(player.uniqueId)
    }

    @EventHandler
    fun onClaim(event: BlockPlaceEvent) {
        val player = event.player

        if (!player.isOp) return
        if (player.inventory.itemInMainHand.itemMeta == null || player.inventory.itemInMainHand.itemMeta.displayName() == null) return
        if (player.inventory.itemInMainHand.itemMeta.displayName() != Translator.getTranslation(TranslationKey.CLAIM_ITEM_NAME))
            return

        player.inventory.remove(Material.SCULK_CATALYST)
        plugin.regionManager.initRegion(player)
    }
}