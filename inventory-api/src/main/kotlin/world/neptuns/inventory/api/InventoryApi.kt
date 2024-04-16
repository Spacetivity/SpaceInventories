package world.neptuns.inventory.api

import net.kyori.adventure.text.Component
import world.neptuns.inventory.api.inventory.InventoryHandler
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

interface InventoryApi {

    val inventoryHandler: InventoryHandler

    fun openConfirmationInventory(
        holder: Player,
        title: Component,
        displayItem: ItemStack,
        onAccept: Consumer<ItemStack>,
        onDeny: Consumer<ItemStack>
    )

}