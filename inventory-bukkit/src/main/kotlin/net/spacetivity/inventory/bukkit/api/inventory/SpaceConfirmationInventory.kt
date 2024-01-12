package net.spacetivity.inventory.bukkit.api.inventory

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.spacetivity.inventory.api.inventory.InventoryController
import net.spacetivity.inventory.api.inventory.InventoryProvider
import net.spacetivity.inventory.api.item.InteractiveItem
import net.spacetivity.inventory.api.item.InventoryPosition
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class SpaceConfirmationInventory(
    val inventoryId: String,
    val title: Component,
    val displayItem: ItemStack,
    val onAccept: () -> Unit,
    val onDeny: () -> Unit
) : InventoryProvider {

    override fun init(player: Player, controller: InventoryController) {
        val acceptItem = ItemStack(Material.LIME_STAINED_GLASS_PANE)
        acceptItem.editMeta { itemMeta: ItemMeta ->
            itemMeta.displayName(Component.text("✔", NamedTextColor.GREEN))
        }

        val denyItem = ItemStack(Material.RED_STAINED_GLASS_PANE)
        denyItem.editMeta { itemMeta: ItemMeta ->
            itemMeta.displayName(Component.text("✗", NamedTextColor.RED))
        }

        controller.fill(
            InventoryController.FillType.RECTANGLE,
            InteractiveItem.of(acceptItem) { _, _, _ -> onAccept.invoke() },
            InventoryPosition.of(0, 0),
            InventoryPosition.of(2, 2)
        )
        controller.fill(
            InventoryController.FillType.RECTANGLE,
            InteractiveItem.of(denyItem) { _, _, _ -> onDeny.invoke() },
            InventoryPosition.of(0, 6),
            InventoryPosition.of(2, 8)
        )
        controller.setItem(1, 4, InteractiveItem.of(this.displayItem))
    }
}
