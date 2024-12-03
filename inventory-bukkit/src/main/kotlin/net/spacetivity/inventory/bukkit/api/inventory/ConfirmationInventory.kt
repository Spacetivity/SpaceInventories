package net.spacetivity.inventory.bukkit.api.inventory

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.spacetivity.inventory.api.inventory.InventoryController
import net.spacetivity.inventory.api.inventory.InventoryProperties
import net.spacetivity.inventory.api.inventory.InventoryProvider
import net.spacetivity.inventory.api.item.InteractiveItem
import net.spacetivity.inventory.api.item.InventoryPos
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

@InventoryProperties(id = "confirmation_inv", rows = 3, columns = 9, closeable = true)
class ConfirmationInventory(
    private val displayItem: ItemStack,
    private val onAccept: ((ItemStack) -> Unit),
    private val onDeny: ((ItemStack) -> Unit)
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
            InteractiveItem.of(acceptItem) { _, _, _ -> onAccept.invoke(acceptItem) },
            InventoryPos.of(0, 0),
            InventoryPos.of(2, 2)
        )

        controller.fill(
            InventoryController.FillType.RECTANGLE,
            InteractiveItem.of(denyItem) { _, _, _ -> onDeny.invoke(denyItem) },
            InventoryPos.of(0, 6),
            InventoryPos.of(2, 8)
        )

        controller.setItem(1, 4, InteractiveItem.of(this.displayItem))
    }
}
