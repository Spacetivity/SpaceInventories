package world.neptuns.inventory.bukkit.api.inventory

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import world.neptuns.inventory.api.annotation.InventoryProperties
import world.neptuns.inventory.api.inventory.InventoryController
import world.neptuns.inventory.api.inventory.InventoryProvider
import world.neptuns.inventory.api.item.InteractiveItem
import world.neptuns.inventory.api.item.InventoryPosition
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.function.Consumer

@InventoryProperties(id = "confirmation_inv", rows = 3, columns = 9, closeable = true)
class SpaceConfirmationInventory(
    val displayItem: ItemStack,
    val onAccept: Consumer<ItemStack>,
    val onDeny: Consumer<ItemStack>
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
            InteractiveItem.of(acceptItem) { _, _, _ -> onAccept.accept(acceptItem) },
            InventoryPosition.of(0, 0),
            InventoryPosition.of(2, 2)
        )
        controller.fill(
            InventoryController.FillType.RECTANGLE,
            InteractiveItem.of(denyItem) { _, _, _ -> onDeny.accept(denyItem) },
            InventoryPosition.of(0, 6),
            InventoryPosition.of(2, 8)
        )
        controller.setItem(1, 4, InteractiveItem.of(this.displayItem))
    }
}
