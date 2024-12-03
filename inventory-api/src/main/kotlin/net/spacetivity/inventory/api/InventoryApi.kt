package net.spacetivity.inventory.api

import net.kyori.adventure.text.Component
import net.spacetivity.inventory.api.inventory.InventoryHandler
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface InventoryApi {

    val inventoryHandler: InventoryHandler

    fun openConfirmationInventory(
        holder: Player,
        title: Component,
        displayItem: ItemStack,
        onAccept: ((ItemStack) -> Unit),
        onDeny: ((ItemStack) -> Unit)
    )

}