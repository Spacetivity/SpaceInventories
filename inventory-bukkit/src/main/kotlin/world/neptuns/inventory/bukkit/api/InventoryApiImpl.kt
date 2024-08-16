package world.neptuns.inventory.bukkit.api

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import world.neptuns.inventory.api.InventoryApi
import world.neptuns.inventory.api.inventory.InventoryHandler
import world.neptuns.inventory.bukkit.api.inventory.InventoryHandlerImpl
import world.neptuns.inventory.bukkit.api.inventory.NeptunConfirmationInventory

class InventoryApiImpl : InventoryApi {

    override val inventoryHandler: InventoryHandler = InventoryHandlerImpl()

    override fun openConfirmationInventory(holder: Player, title: Component, displayItem: ItemStack, onAccept: ((ItemStack) -> Unit), onDeny: ((ItemStack) -> Unit)) {
        inventoryHandler.openStaticInventory(
            holder,
            title,
            NeptunConfirmationInventory(displayItem, onAccept, onDeny),
            true
        )
    }

}
