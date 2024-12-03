package net.spacetivity.inventory.bukkit.api

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import net.spacetivity.inventory.api.inventory.InventoryHandler
import net.spacetivity.inventory.bukkit.api.inventory.InventoryHandlerImpl
import net.spacetivity.inventory.bukkit.api.inventory.ConfirmationInventory

class InventoryApiImpl : net.spacetivity.inventory.api.InventoryApi {

    override val inventoryHandler: InventoryHandler = InventoryHandlerImpl()

    override fun openConfirmationInventory(holder: Player, title: Component, displayItem: ItemStack, onAccept: ((ItemStack) -> Unit), onDeny: ((ItemStack) -> Unit)) {
        inventoryHandler.openStaticInventory(
            holder = holder,
            title = title,
            provider = ConfirmationInventory(displayItem, onAccept, onDeny),
            forceSyncOpening = true
        )
    }

}
