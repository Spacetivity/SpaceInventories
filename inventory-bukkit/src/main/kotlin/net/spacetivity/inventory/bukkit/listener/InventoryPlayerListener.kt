package net.spacetivity.inventory.bukkit.listener

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.spacetivity.inventory.api.SpaceInventoryProvider
import net.spacetivity.inventory.api.inventory.InventoryHandler
import net.spacetivity.inventory.api.inventory.SpaceInventory
import net.spacetivity.inventory.api.item.InteractiveItem
import net.spacetivity.inventory.api.item.InventoryPosition
import net.spacetivity.inventory.api.utils.MathUtils
import net.spacetivity.inventory.bukkit.SpaceInventoryBukkit
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerQuitEvent

class InventoryPlayerListener(private val plugin: SpaceInventoryBukkit) : Listener {

    private val inventoryHandler: InventoryHandler

    init {
        plugin.server.pluginManager.registerEvents(this, this.plugin)
        this.inventoryHandler = SpaceInventoryProvider.api.inventoryHandler
    }

    @EventHandler
    fun onPlayerInventoryClick(event: InventoryClickEvent) {
        val player: Player = event.whoClicked as Player

        if (!player.hasMetadata("open-inventory")) return
        if (event.clickedInventory !== player.openInventory.topInventory) return
        if (!validateInventory(player, event.view.title())) return

        val inventory: SpaceInventory = inventoryHandler.getInventory(player, getOpenInventoryName(player)) ?: return
        val position: InventoryPosition = MathUtils.slotToPosition(event.slot, inventory.columns)

        event.isCancelled = true

        val currentItem: InteractiveItem = inventory.controller.getItem(position) ?: return
        currentItem.runAction(position, currentItem, event)
    }

    @EventHandler
    fun onPlayerInventoryClose(event: InventoryCloseEvent) {
        if (event.inventory.holder !is Player) return
        val player: Player = event.player as Player

        if (!player.hasMetadata("open-inventory")) return
        if (!validateInventory(player, event.view.title())) return

        val inventory: SpaceInventory =
            inventoryHandler.getInventory(player, getOpenInventoryName(player)) ?: return

        if (!inventory.isCloseable) {
            Bukkit.getScheduler().runTask(this.plugin, Runnable { inventory.open(player) })
        } else {
            player.removeMetadata("open-inventory", this.plugin)
            if (inventory.isStaticInventory) inventoryHandler.removeCachedInventory(player, inventory)
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        inventoryHandler.clearCachedInventories(event.player)
    }

    private fun validateInventory(player: Player, title: Component): Boolean {
        val openInventoryTitle: String = PlainTextComponentSerializer.plainText().serialize(title)

        val inventoryName: String = getOpenInventoryName(player)
        val inventory: SpaceInventory = inventoryHandler.getInventory(player, inventoryName) ?: return false
        val possibleInventoryTitle: String = PlainTextComponentSerializer.plainText().serialize(inventory.title)

        return possibleInventoryTitle.equals(openInventoryTitle, true)
    }

    private fun getOpenInventoryName(player: Player): String {
        return player.getMetadata("open-inventory")[0].value() as String
    }
}
