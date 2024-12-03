package net.spacetivity.inventory.api.inventory

import org.bukkit.Material
import org.bukkit.inventory.Inventory
import net.spacetivity.inventory.api.item.InteractiveItem
import net.spacetivity.inventory.api.item.InventoryPos
import net.spacetivity.inventory.api.pagination.InventoryPagination

interface InventoryController {

    val provider: net.spacetivity.inventory.api.inventory.InventoryProvider
    val properties: net.spacetivity.inventory.api.inventory.InventoryProperties

    val inventorySlotCount: Int
    var isCloseable: Boolean

    val contents: Map<net.spacetivity.inventory.api.item.InventoryPos, net.spacetivity.inventory.api.item.InteractiveItem?>
    val pagination: InventoryPagination?
    var rawInventory: Inventory?

    var overriddenInventoryId: String?
    var overriddenRows: Int
    var overriddenColumns: Int

    fun getInventoryId(): String
    fun getRows(): Int
    fun getColumns(): Int

    fun constructEmptyContent()

    fun placeholder(pos: net.spacetivity.inventory.api.item.InventoryPos, type: Material)
    fun placeholder(row: Int, column: Int, type: Material)

    fun setItem(pos: net.spacetivity.inventory.api.item.InventoryPos, item: net.spacetivity.inventory.api.item.InteractiveItem)
    fun setItem(row: Int, column: Int, item: net.spacetivity.inventory.api.item.InteractiveItem)
    fun addItem(item: net.spacetivity.inventory.api.item.InteractiveItem)
    fun addItemToRandomPosition(item: net.spacetivity.inventory.api.item.InteractiveItem)
    fun removeItem(name: String)
    fun removeItem(type: Material)

    fun fill(fillType: net.spacetivity.inventory.api.inventory.InventoryController.FillType, item: net.spacetivity.inventory.api.item.InteractiveItem, vararg positions: net.spacetivity.inventory.api.item.InventoryPos)
    fun clearPosition(pos: net.spacetivity.inventory.api.item.InventoryPos)

    fun isPositionTaken(pos: net.spacetivity.inventory.api.item.InventoryPos): Boolean
    fun getPositionOfItem(item: net.spacetivity.inventory.api.item.InteractiveItem): net.spacetivity.inventory.api.item.InventoryPos?
    fun getFirstEmptyPosition(): net.spacetivity.inventory.api.item.InventoryPos?

    fun getItem(pos: net.spacetivity.inventory.api.item.InventoryPos): net.spacetivity.inventory.api.item.InteractiveItem?
    fun getItem(row: Int, column: Int): net.spacetivity.inventory.api.item.InteractiveItem?
    fun findFirstItemWithType(type: Material): net.spacetivity.inventory.api.item.InteractiveItem?

    fun createPagination(): InventoryPagination

    fun updateRawInventory()

    enum class FillType {
        ROW,
        RECTANGLE,
        LEFT_BORDER,
        RIGHT_BORDER,
        TOP_BORDER,
        BOTTOM_BORDER,
        ALL_BORDERS
    }
}
