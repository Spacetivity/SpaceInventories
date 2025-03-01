package net.spacetivity.inventory.api.inventory

import net.spacetivity.inventory.api.item.InteractiveItem
import net.spacetivity.inventory.api.item.InventoryPos
import net.spacetivity.inventory.api.pagination.InventoryPagination
import org.bukkit.Material
import org.bukkit.inventory.Inventory

interface InventoryController {

    val provider: InventoryProvider
    val properties: InventoryProperties

    val inventorySlotCount: Int
    var isCloseable: Boolean

    val contents: MutableMap<InventoryPos, InteractiveItem?>
    val pagination: InventoryPagination?
    var rawInventory: Inventory?

    var overriddenInventoryId: String?
    var overriddenRows: Int
    var overriddenColumns: Int

    fun getInventoryId(): String
    fun getRows(): Int
    fun getColumns(): Int

    fun placeholder(pos: InventoryPos, type: Material)
    fun placeholder(row: Int, column: Int, type: Material)

    fun setItem(pos: InventoryPos, item: InteractiveItem)
    fun setItem(row: Int, column: Int, item: InteractiveItem)
    fun addItem(item: InteractiveItem)
    fun addItemToRandomPosition(item: InteractiveItem)
    fun removeItem(name: String)
    fun removeItem(type: Material)

    fun fill(fillType: FillType, item: InteractiveItem, vararg positions: InventoryPos)
    fun clearPosition(pos: InventoryPos)

    fun isPositionTaken(pos: InventoryPos): Boolean
    fun getPositionOfItem(item: InteractiveItem): InventoryPos?
    fun getFirstEmptyPosition(): InventoryPos?

    fun getItem(pos: InventoryPos): InteractiveItem?
    fun getItem(row: Int, column: Int): InteractiveItem?
    fun findFirstItemWithType(type: Material): InteractiveItem?

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
