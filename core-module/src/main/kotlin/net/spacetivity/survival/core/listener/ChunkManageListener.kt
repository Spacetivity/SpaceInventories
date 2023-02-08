package net.spacetivity.survival.core.listener

import net.spacetivity.survival.core.SpaceSurvivalPlugin
import net.spacetivity.survival.core.translation.TranslationKey
import net.spacetivity.survival.core.translation.Translator
import net.spacetivity.survival.core.utils.ItemBuilder
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemFlag

class ChunkManageListener(private var plugin: SpaceSurvivalPlugin) : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        plugin.inventoryManager.loadInventory(player)
        plugin.chunkManager.loadClaimedChunks(player.uniqueId)
        plugin.landManager.loadLand(player.uniqueId)

        val originalChunk = plugin.chunkManager.getOriginalChunk(player.uniqueId)

        player.teleport(Location(player.world, originalChunk!!.first * 16.0, 100.0, originalChunk.second * 16.0))

        if (plugin.landManager.getLand(player.uniqueId) == null) player.inventory.setItem(
            4, ItemBuilder(Material.SCULK_CATALYST)
                .setName(Translator.getTranslation(TranslationKey.CLAIM_ITEM_NAME))
                .addEnchantment(Enchantment.DURABILITY, 1)
                .setLoreByString(
                    mutableListOf(
                        "Place this block to claim your",
                        "first chunk in the survival world."
                    )
                )
                .addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                .build()
        )

    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        plugin.inventoryManager.saveInventory(player)
        plugin.chunkManager.unregisterRegisteredChunks(player.uniqueId)
        plugin.landManager.unregisterLand(player.uniqueId)
    }

    @EventHandler
    fun onClaim(event: BlockPlaceEvent) {
        val player = event.player

        if (!player.isOp) return
        if (player.inventory.itemInMainHand.itemMeta == null || player.inventory.itemInMainHand.itemMeta.displayName() == null) return
        if (player.inventory.itemInMainHand.itemMeta.displayName() != Translator.getTranslation(TranslationKey.CLAIM_ITEM_NAME))
            return

        player.inventory.remove(Material.SCULK_CATALYST)
        plugin.landManager.initLand(player)
    }
}