package world.neptuns.inventory.bukkit.listener

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerQuitEvent
import world.neptuns.inventory.api.NeptunInventoryProvider
import world.neptuns.inventory.api.inventory.InventoryHandler
import world.neptuns.inventory.api.inventory.NeptunInventory
import world.neptuns.inventory.api.item.InteractiveItem
import world.neptuns.inventory.api.item.InventoryPos
import world.neptuns.inventory.api.utils.MathUtils
import world.neptuns.inventory.bukkit.utils.SoundUtils
import world.neptuns.inventory.bukkit.NeptunInventoryBukkit

class InventoryPlayerListener(private val plugin: NeptunInventoryBukkit) : Listener {

    private val inventoryHandler: InventoryHandler

    init {
        plugin.server.pluginManager.registerEvents(this, this.plugin)
        this.inventoryHandler = NeptunInventoryProvider.api.inventoryHandler
    }

    @EventHandler
    fun onPlayerInventoryClick(event: InventoryClickEvent) {
        val player: Player = event.whoClicked as Player

        if (!player.hasMetadata("open-inventory")) return
        if (event.clickedInventory !== player.openInventory.topInventory) return
        if (!validateInventory(player, event.view.title())) return

        val inventory: NeptunInventory = inventoryHandler.getInventory(player, getOpenInventoryName(player)) ?: return
        val position: InventoryPos = MathUtils.slotToPosition(event.slot, inventory.columns)

        event.isCancelled = true

        val currentItem: InteractiveItem = inventory.controller.getItem(position) ?: return
        currentItem.runAction(position, currentItem, event)

        if (inventory.controller.properties.playSoundOnClick) SoundUtils.playClickSound(player)
    }

    @EventHandler
    fun onPlayerInventoryClose(event: InventoryCloseEvent) {
        if (event.inventory.holder !is Player) return
        val player: Player = event.player as Player

        if (!player.hasMetadata("open-inventory")) return
        if (!validateInventory(player, event.view.title())) return

        val inventory = inventoryHandler.getInventory(player, getOpenInventoryName(player)) ?: return

        if (!inventory.isCloseable) {
            Bukkit.getScheduler().runTask(this.plugin, Runnable { inventory.open(player) })
        } else {
            if (inventory.controller.properties.playSoundOnClose) SoundUtils.playSound(player, SoundUtils.CLOSE)
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
        val inventory: NeptunInventory = inventoryHandler.getInventory(player, inventoryName) ?: return false
        val possibleInventoryTitle: String = PlainTextComponentSerializer.plainText().serialize(inventory.title)

        return possibleInventoryTitle.equals(openInventoryTitle, true)
    }

    private fun getOpenInventoryName(player: Player): String {
        return player.getMetadata("open-inventory")[0].value() as String
    }
}
