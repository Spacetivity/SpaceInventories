package net.spacetivity.inventory.bukkit.api

import net.kyori.adventure.text.Component
import net.spacetivity.inventory.api.InventoryApi
import net.spacetivity.inventory.api.inventory.InventoryHandler
import net.spacetivity.inventory.bukkit.api.inventory.InventoryHandlerImpl
import net.spacetivity.inventory.bukkit.api.inventory.SpaceConfirmationInventory
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

class InventoryApiImpl : InventoryApi {

    override val inventoryHandler: InventoryHandler = InventoryHandlerImpl()

    override fun openConfirmationInventory(
        holder: Player,
        title: Component,
        displayItem: ItemStack,
        onAccept: () -> Unit,
        onDeny: () -> Unit
    ) {
        val key = "confirmation-inv${UUID.randomUUID().toString().split("-")[0]}"

        inventoryHandler.openStaticInventory(
            holder,
            title,
            SpaceConfirmationInventory(key, title, displayItem, onAccept, onDeny),
            true
        )
    }

}
