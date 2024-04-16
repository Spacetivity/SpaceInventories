package world.neptuns.inventory.bukkit.api

import net.kyori.adventure.text.Component
import world.neptuns.inventory.api.InventoryApi
import world.neptuns.inventory.api.inventory.InventoryHandler
import world.neptuns.inventory.bukkit.api.inventory.InventoryHandlerImpl
import world.neptuns.inventory.bukkit.api.inventory.SpaceConfirmationInventory
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
