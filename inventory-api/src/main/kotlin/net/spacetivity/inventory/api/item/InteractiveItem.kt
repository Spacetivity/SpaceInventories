package net.spacetivity.inventory.api.item

import net.kyori.adventure.text.Component
import net.spacetivity.inventory.api.SpaceInventoryProvider
import net.spacetivity.inventory.api.inventory.InventoryController
import net.spacetivity.inventory.api.pagination.InventoryPagination
import net.spacetivity.inventory.api.utils.MathUtils
import org.apache.logging.log4j.util.TriConsumer
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
import java.util.*

@Suppress("UNCHECKED_CAST")
class InteractiveItem(
    val item: ItemStack,
    private val action: TriConsumer<InventoryPosition, InteractiveItem, InventoryClickEvent>?
) {

    private val itemId = UUID.randomUUID().toString().split("-")[0]

    fun runAction(position: InventoryPosition, interactiveItem: InteractiveItem, event: InventoryClickEvent) {
        action?.accept(position, interactiveItem, event)
    }

    fun update(controller: InventoryController, modification: Modification, vararg values: Any) {
        if (values.size > 1) throw UnsupportedOperationException("There are no more than one value allowed! Current size: " + values.size)

        val inventoryPosition: InventoryPosition? = controller.getPositionOfItem(this)
        val modifiableItem: ItemStack
        var extraItem: ItemStack? = null

        if (inventoryPosition != null) {
            val slot: Int = MathUtils.positionToSlot(inventoryPosition, controller.getColumns())
            val rawInventory: Inventory = controller.rawInventory!!
            modifiableItem = rawInventory.getItem(slot) ?: this.item
        } else {
            modifiableItem = this.item
        }

        if (controller.pagination != null)
            extraItem = controller.pagination!!.items.values().find { it.itemId == this.itemId }!!.item

        val newValue: Any = values[0]

        when (modification) {
            Modification.TYPE -> {
                if (newValue !is Material) throw UnsupportedOperationException("'newValue' is not a Material!")
                modifiableItem.type = newValue

                extraItem?.type = newValue
            }

            Modification.DISPLAY_NAME -> {
                if (newValue !is Component) throw UnsupportedOperationException("'newValue' is not a Component!")
                modifiableItem.editMeta { itemMeta: ItemMeta -> itemMeta.displayName(newValue) }

                extraItem?.editMeta { itemMeta: ItemMeta -> itemMeta.displayName(newValue) }
            }

            Modification.LORE -> {
                if (newValue !is List<*>) {
                    throw UnsupportedOperationException("'newValue' is not a List!")
                }

                modifiableItem.editMeta { itemMeta ->
                    val lore = newValue.toMutableList()
                    itemMeta.lore(lore as MutableList<Component>)
                }

                extraItem?.editMeta { itemMeta ->
                    val lore = newValue.toMutableList()
                    itemMeta.lore(lore as MutableList<Component>)
                }
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
                    modifiableItem.addUnsafeEnchantment(Enchantment.DURABILITY, 1)
                    extraItem?.addUnsafeEnchantment(Enchantment.DURABILITY, 1)
                } else {
                    modifiableItem.editMeta {
                        it.enchants.map { e -> e.key }.forEach { n -> println(n.key.key) }
                        it.removeEnchantments()
                    }

                    extraItem?.editMeta {
                        it.enchants.map { e -> e.key }.forEach { n -> println(n.key.key) }
                        it.removeEnchantments()
                    }
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
        fun placeholder(material: Material): InteractiveItem {
            val itemId = UUID.randomUUID().toString().split("-")[0]
            return InteractiveItem(makeItemStack(material, itemId)) { _, _, _ -> }
        }

        fun nextPage(item: ItemStack, pagination: InventoryPagination): InteractiveItem {
            return of(item) { _: InventoryPosition, _: InteractiveItem, _: InventoryClickEvent? -> pagination.toNextPage() }
        }

        fun previousPage(item: ItemStack, pagination: InventoryPagination): InteractiveItem {
            return of(item) { _: InventoryPosition, _: InteractiveItem, _: InventoryClickEvent -> pagination.toPreviousPage() }
        }

        fun navigator(item: ItemStack, inventoryKey: String): InteractiveItem {
            return of(item) { _: InventoryPosition, _: InteractiveItem, event: InventoryClickEvent ->
                val holder: Player = event.whoClicked as Player

                val elytraInventory =
                    SpaceInventoryProvider.api.inventoryHandler.getInventory(holder, inventoryKey)
                        ?: throw NullPointerException("No inventory with key $inventoryKey found!")

                holder.closeInventory()
                elytraInventory.open(holder)
            }
        }


        fun of(item: ItemStack): InteractiveItem {
            return InteractiveItem(item) { _: InventoryPosition, _: InteractiveItem, _: InventoryClickEvent -> }
        }

        fun of(item: ItemStack, action: TriConsumer<InventoryPosition, InteractiveItem, InventoryClickEvent>): InteractiveItem {
            return InteractiveItem(item, action)
        }

        fun of(item: ItemStack, command: String): InteractiveItem {
            return InteractiveItem(item) { _, _, event ->
                val player: Player = event.whoClicked as Player
                player.performCommand(command)
            }
        }

        private fun makeItemStack(material: Material, itemId: String): ItemStack {
            val itemStack = ItemStack(material)
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
