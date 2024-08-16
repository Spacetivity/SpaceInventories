package world.neptuns.inventory.api

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import world.neptuns.inventory.api.inventory.InventoryHandler

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