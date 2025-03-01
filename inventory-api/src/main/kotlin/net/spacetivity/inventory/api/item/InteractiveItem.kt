package net.spacetivity.inventory.api.item

import net.kyori.adventure.text.Component
import net.spacetivity.inventory.api.extension.getInventory
import net.spacetivity.inventory.api.inventory.InventoryController
import net.spacetivity.inventory.api.pagination.InventoryPagination
import net.spacetivity.inventory.api.utils.MathUtils
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import java.time.Instant
import java.util.*

class InteractiveItem(
    var item: ItemStack,
    private val action: ((InventoryPos, InteractiveItem, InventoryClickEvent) -> Unit)?
) {

    private val itemId = UUID.randomUUID().toString().split("-")[0]
    private var lastClickTime: Instant = Instant.EPOCH
    private var cooldownMillis: Long = 150

    fun runAction(position: InventoryPos, interactiveItem: InteractiveItem, event: InventoryClickEvent) {
        val now = Instant.now()

        if (now.toEpochMilli() - Instant.EPOCH.toEpochMilli() < cooldownMillis) {
            event.isCancelled = true
            return
        }

        lastClickTime = now
        action?.invoke(position, interactiveItem, event)
    }

    @Suppress("UNCHECKED_CAST")
    fun update(controller: InventoryController, modification: Modification, vararg values: Any) {
        if (values.size > 1) throw UnsupportedOperationException("There are no more than one value allowed! Current size: " + values.size)

        val inventoryPosition: InventoryPos? = controller.getPositionOfItem(this)
        var modifiableItem: ItemStack
        var extraItem: ItemStack? = null // is a reference for the extra stored item in the pagination items if existing

        if (inventoryPosition != null) {
            val slot: Int = MathUtils.positionToSlot(inventoryPosition, controller.getColumns())
            val rawInventory: Inventory = controller.rawInventory!!
            modifiableItem = rawInventory.getItem(slot) ?: this.item
        } else {
            modifiableItem = this.item
        }

        if (controller.pagination != null)
            extraItem = controller.pagination!!.items.values().find { it.itemId == this.itemId }?.item

        val newValue = values[0]

        when (modification) {
            Modification.TYPE -> {
                if (newValue !is Material) throw UnsupportedOperationException("'newValue' is not a Material!")

                modifiableItem = modifiableItem.withType(newValue)
                this.item = modifiableItem

                extraItem = extraItem?.withType(newValue)
                if (extraItem != null) controller.pagination?.items?.values()?.find { it.itemId == this.itemId }?.item = extraItem
            }

            Modification.DISPLAY_NAME -> {
                if (newValue !is Component) throw UnsupportedOperationException("'newValue' is not a Component!")

                modifiableItem.editMeta { itemMeta: ItemMeta -> itemMeta.displayName(newValue) }
                extraItem?.editMeta { itemMeta: ItemMeta -> itemMeta.displayName(newValue) }
            }

            Modification.LORE -> {
                if (newValue !is MutableList<*>) throw UnsupportedOperationException("'newValue' is not a List!")

                modifiableItem.editMeta { it.lore(newValue as MutableList<Component>) }
                extraItem?.editMeta { it.lore(newValue as MutableList<Component>) }
            }

            Modification.AMOUNT -> {
                if (newValue !is Int) throw UnsupportedOperationException("'newValue' is not an Integer!")

                modifiableItem.amount = newValue
                if (extraItem != null) extraItem.amount = newValue
            }

            Modification.INCREMENT -> {
                if (newValue !is Int) throw UnsupportedOperationException("'newValue' is not an Integer!")

                modifiableItem.amount += newValue
                if (extraItem != null) extraItem.amount += newValue
            }

            Modification.ENCHANTMENTS -> {
                if (newValue !is ItemEnchantment) throw UnsupportedOperationException("'newValue' is not an ItemEnchantment!")

                newValue.performAction(modifiableItem)
                if (extraItem != null) newValue.performAction(extraItem)
            }

            Modification.GLOWING -> {
                if (newValue !is Boolean) throw UnsupportedOperationException("'newValue' is not an Boolean!")

                if (newValue) {
                    modifiableItem.addUnsafeEnchantment(Enchantment.UNBREAKING, 1)
                    extraItem?.addUnsafeEnchantment(Enchantment.UNBREAKING, 1)
                } else {
                    modifiableItem.editMeta { it.removeEnchantments() }
                    extraItem?.editMeta { it.removeEnchantments() }
                }
            }
        }
    }

    enum class Modification {
        TYPE,
        DISPLAY_NAME,
        LORE,
        AMOUNT,
        INCREMENT,
        ENCHANTMENTS,
        GLOWING;
    }

    companion object {

        fun placeholder(type: Material): InteractiveItem {
            val itemId = UUID.randomUUID().toString().split("-")[0]
            return InteractiveItem(makeItemStack(type, itemId)) { _, _, _ -> }
        }

        fun nextPage(item: ItemStack, pagination: InventoryPagination): InteractiveItem {
            return of(item) { _: InventoryPos, _: InteractiveItem, _: InventoryClickEvent? -> pagination.toNextPage() }
        }

        fun previousPage(item: ItemStack, pagination: InventoryPagination): InteractiveItem {
            return of(item) { _: InventoryPos, _: InteractiveItem, _: InventoryClickEvent -> pagination.toPreviousPage() }
        }

        fun navigator(item: ItemStack, inventoryKey: String): InteractiveItem {
            return of(item) { _: InventoryPos, _: InteractiveItem, event: InventoryClickEvent ->
                val holder: Player = event.whoClicked as Player
                val inventory = getInventory(holder, inventoryKey) ?: return@of

                holder.closeInventory()
                inventory.open(holder)
            }
        }

        fun of(item: ItemStack): InteractiveItem {
            return InteractiveItem(item) { _: InventoryPos, _: InteractiveItem, _: InventoryClickEvent -> }
        }

        fun of(item: ItemStack, action: ((InventoryPos, InteractiveItem, InventoryClickEvent) -> Unit)): InteractiveItem {
            return InteractiveItem(item, action)
        }

        private fun makeItemStack(type: Material, itemId: String): ItemStack {
            val itemStack = ItemStack(type)
            val itemMeta: ItemMeta = itemStack.itemMeta
            itemMeta.displayName(Component.text(" "))

            val namespacedKey = NamespacedKey("item", "itemid")
            val dataContainer: PersistentDataContainer = itemMeta.persistentDataContainer
            if (!dataContainer.has(namespacedKey, PersistentDataType.STRING)) {
                dataContainer.set(namespacedKey, PersistentDataType.STRING, itemId)
                itemStack.setItemMeta(itemMeta)
            }

            itemStack.setItemMeta(itemMeta)
            return itemStack
        }
    }
}
