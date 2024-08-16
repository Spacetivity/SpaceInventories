package world.neptuns.inventory.api.extension

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import world.neptuns.inventory.api.NeptunInventoryProvider
import world.neptuns.inventory.api.inventory.InventoryProvider
import world.neptuns.inventory.api.inventory.NeptunInventory

fun openStaticInventory(holder: Player, title: Component, provider: InventoryProvider) {
    NeptunInventoryProvider.api.inventoryHandler.openStaticInventory(holder, title, provider, true)
}

fun openInventory(holder: Player, key: String) {
    NeptunInventoryProvider.api.inventoryHandler.getInventory(holder, key)?.open(holder)
}

fun getInventory(holder: Player, key: String): NeptunInventory? {
    return NeptunInventoryProvider.api.inventoryHandler.getInventory(holder, key)
}

fun cacheInventory(holder: Player, title: Component, provider: InventoryProvider) {
    NeptunInventoryProvider.api.inventoryHandler.cacheInventory(holder, title, provider)
}

fun removeCachedInventory(holder: Player, inventory: NeptunInventory) {
    NeptunInventoryProvider.api.inventoryHandler.removeCachedInventory(holder, inventory)
}

fun clearCachedInventories(holder: Player) {
    NeptunInventoryProvider.api.inventoryHandler.clearCachedInventories(holder)
}

fun updateCachedInventory(holder: Player, key: String) {
    NeptunInventoryProvider.api.inventoryHandler.updateCachedInventory(holder, key)
}