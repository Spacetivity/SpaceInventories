package net.spacetivity.inventory.api.extension

import net.kyori.adventure.text.Component
import net.spacetivity.inventory.api.SpaceInventoryProvider
import net.spacetivity.inventory.api.inventory.InventoryProvider
import net.spacetivity.inventory.api.inventory.SpaceInventory
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun Bukkit.openStaticInventory(holder: Player, title: Component, provider: InventoryProvider) {
    SpaceInventoryProvider.api.inventoryHandler.openStaticInventory(holder, title, provider, true)
}

fun Bukkit.openInventory(holder: Player, key: String) {
    SpaceInventoryProvider.api.inventoryHandler.getInventory(holder, key)?.open(holder)
}

fun Bukkit.getInventory(holder: Player, key: String): SpaceInventory? {
    return SpaceInventoryProvider.api.inventoryHandler.getInventory(holder, key)
}

fun Bukkit.cacheInventory(holder: Player, title: Component, provider: InventoryProvider) {
    SpaceInventoryProvider.api.inventoryHandler.cacheInventory(holder, title, provider)
}

fun Bukkit.removeCachedInventory(holder: Player, inventory: SpaceInventory) {
    SpaceInventoryProvider.api.inventoryHandler.removeCachedInventory(holder, inventory)
}

fun Bukkit.clearCachedInventories(holder: Player) {
    SpaceInventoryProvider.api.inventoryHandler.clearCachedInventories(holder)
}

fun Bukkit.updateCachedInventory(holder: Player, key: String) {
    SpaceInventoryProvider.api.inventoryHandler.updateCachedInventory(holder, key)
}