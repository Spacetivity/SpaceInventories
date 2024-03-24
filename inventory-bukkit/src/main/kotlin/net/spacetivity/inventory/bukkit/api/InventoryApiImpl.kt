package net.spacetivity.inventory.bukkit.api

import net.kyori.adventure.text.Component
import net.spacetivity.inventory.api.InventoryApi
import net.spacetivity.inventory.api.inventory.InventoryHandler
import net.spacetivity.inventory.bukkit.api.inventory.InventoryHandlerImpl
import net.spacetivity.inventory.bukkit.api.inventory.SpaceConfirmationInventory
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

class InventoryApiImpl : InventoryApi {

    override val inventoryHandler: InventoryHandler = InventoryHandlerImpl()

    override fun openConfirmationInventory(
        holder: Player,
        title: Component,
        displayItem: ItemStack,
        onAccept: Consumer<ItemStack>,
        onDeny: Consumer<ItemStack>
    ) {
        inventoryHandler.openStaticInventory(
            holder,
            title,
            SpaceConfirmationInventory(displayItem, onAccept, onDeny),
            true
        )
    }

}
