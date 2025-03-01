package net.spacetivity.inventory.bukkit.api.inventory

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.spacetivity.inventory.api.inventory.InventoryController
import net.spacetivity.inventory.api.inventory.InventoryProperties
import net.spacetivity.inventory.api.inventory.InventoryProvider
import net.spacetivity.inventory.api.item.InteractiveItem
import net.spacetivity.inventory.api.item.InventoryPos
import net.spacetivity.inventory.api.pagination.InventoryPagination
import net.spacetivity.inventory.api.utils.MathUtils
import net.spacetivity.inventory.bukkit.api.pagination.InventoryPaginationImpl
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import java.util.concurrent.ThreadLocalRandom

class InventoryControllerImpl(override val provider: InventoryProvider) : InventoryController {

    override val properties: InventoryProperties = provider.javaClass.getAnnotation(InventoryProperties::class.java)

    override val inventorySlotCount: Int = properties.rows * properties.columns
    override var isCloseable: Boolean = properties.closeable

    override val contents: MutableMap<InventoryPos, InteractiveItem?> = mutableMapOf()
    override var pagination: InventoryPagination? = null
    override var rawInventory: Inventory? = null

    override var overriddenInventoryId: String? = null
    override var overriddenRows: Int = 0
    override var overriddenColumns: Int = 0

    override fun getInventoryId(): String {
        return if (overriddenInventoryId == null) this.properties.id else overriddenInventoryId!!
    }

    override fun getRows(): Int {
        return if (overriddenRows == 0) this.properties.rows else overriddenRows
    }

    override fun getColumns(): Int {
        return if (overriddenColumns == 0) this.properties.columns else overriddenColumns
    }

    override fun placeholder(pos: InventoryPos, type: Material) {
        setItem(pos, InteractiveItem.placeholder(type))
    }

    override fun placeholder(row: Int, column: Int, type: Material) {
        setItem(row, column, InteractiveItem.placeholder(type))
    }

    override fun setItem(pos: InventoryPos, item: InteractiveItem) {
        contents.replace(pos, item)
    }

    override fun setItem(row: Int, column: Int, item: InteractiveItem) {
        contents.replace(InventoryPos.of(row, column), item)
    }

    override fun addItem(item: InteractiveItem) {
        val emptyPosition: InventoryPos = getFirstEmptyPosition() ?: return
        setItem(emptyPosition, item)
    }

    override fun addItemToRandomPosition(item: InteractiveItem) {
        val randomSlotIndex = ThreadLocalRandom.current().nextInt(inventorySlotCount)
        val randomPosition: InventoryPos = MathUtils.slotToPosition(randomSlotIndex, getColumns())
        if (isPositionTaken(randomPosition)) return
        setItem(randomPosition, item)
    }

    override fun removeItem(name: String) {
        val tempEntries: Set<Map.Entry<InventoryPos, InteractiveItem?>> =
            contents.entries
        for ((position, value) in tempEntries) {
            val interactiveItem: InteractiveItem = value ?: continue

            val serializer: PlainTextComponentSerializer = PlainTextComponentSerializer.plainText()
            if (!serializer.serialize(interactiveItem.item.displayName()).equals(name, true)) continue

            contents.replace(position, null)
            this.rawInventory?.remove(interactiveItem.item)
        }
    }

    override fun removeItem(type: Material) {
        val tempEntries: Set<Map.Entry<InventoryPos, InteractiveItem?>> = contents.entries

        for ((position, value) in tempEntries) {
            val interactiveItem: InteractiveItem = value ?: return
            if (interactiveItem.item.type != type) continue

            contents.replace(position, null)
        }
    }

    override fun fill(fillType: InventoryController.FillType, item: InteractiveItem, vararg positions: InventoryPos) {
        val rows: Int = getRows()
        val columns: Int = getColumns()

        when (fillType) {
            InventoryController.FillType.ROW -> {
                require(positions.size <= 1) { "To fill a row only 1 position is allowed. Used positions: " + positions.size }

                val startSlot: Int = MathUtils.positionToSlot(positions[0], getColumns())

                for (currentSlot in startSlot until (startSlot + columns)) {
                    setItem(MathUtils.slotToPosition(currentSlot, columns), item)
                }
            }

            InventoryController.FillType.RECTANGLE -> {
                require(positions.size == 2) { "Only two positions are allowed to create a rectangle!" }

                val fromPos: InventoryPos = positions[0]
                val toPos: InventoryPos = positions[1]

                val fromRow: Int = fromPos.row
                val fromColumn: Int = fromPos.column
                val toRow: Int = toPos.row
                val toColumn: Int = toPos.column

                for (row in fromRow..toRow) {
                    for (col in fromColumn..toColumn) {
                        setItem(InventoryPos.of(row, col), item)
                    }
                }
            }

            InventoryController.FillType.LEFT_BORDER -> {
                var currentSlot = 0
                while (currentSlot < this.inventorySlotCount) {
                    val currentPosition: InventoryPos = MathUtils.slotToPosition(currentSlot, columns)
                    setItem(currentPosition, item)
                    currentSlot += rows
                }
            }

            InventoryController.FillType.RIGHT_BORDER -> {
                val lastColumnStart = rows - 1
                val lastColumnEnd = this.inventorySlotCount - 1
                var currentSlot = lastColumnStart
                while (currentSlot <= lastColumnEnd) {
                    val currentPos: InventoryPos = MathUtils.slotToPosition(currentSlot, columns)
                    val nextPos: InventoryPos = MathUtils.nextPositionFromSlot(currentSlot, columns)

                    if (currentPos.row == nextPos.row) {
                        currentSlot += rows
                        continue
                    }

                    val currentPosition: InventoryPos = MathUtils.slotToPosition(currentSlot, columns)
                    setItem(currentPosition, item)
                    currentSlot += rows
                }
            }

            InventoryController.FillType.TOP_BORDER -> {
                for (currentSlot in 0 until columns) {
                    val currentPosition: InventoryPos = MathUtils.slotToPosition(currentSlot, columns)
                    setItem(currentPosition, item)
                }
            }

            InventoryController.FillType.BOTTOM_BORDER -> {
                val size = this.inventorySlotCount
                val firstColumnInLastRow = size - columns
                for (currentSlot in firstColumnInLastRow until size) {
                    val currentPosition: InventoryPos = MathUtils.slotToPosition(currentSlot, columns)
                    setItem(currentPosition, item)
                }
            }

            InventoryController.FillType.ALL_BORDERS -> {
                fill(InventoryController.FillType.TOP_BORDER, item)
                fill(InventoryController.FillType.RIGHT_BORDER, item)
                fill(InventoryController.FillType.BOTTOM_BORDER, item)
                fill(InventoryController.FillType.LEFT_BORDER, item)
            }
        }
    }

    override fun clearPosition(pos: InventoryPos) {
        contents.replace(pos, null)
        rawInventory?.clear(MathUtils.positionToSlot(pos, getColumns()))
    }

    override fun isPositionTaken(pos: InventoryPos): Boolean {
        return contents[pos] != null
    }

    override fun getPositionOfItem(item: InteractiveItem): InventoryPos? {
        return contents.entries.filter { it.value == item }.map { it.key }.firstOrNull()
    }

    override fun getFirstEmptyPosition(): InventoryPos? {
        var emptyPosition: InventoryPos? = null

        for (position in this.contents.keys) if (this.contents[position] == null) {
            emptyPosition = position
            break
        }

        return emptyPosition
    }

    override fun getItem(pos: InventoryPos): InteractiveItem? {
        return contents[pos]
    }

    override fun getItem(row: Int, column: Int): InteractiveItem? {
        return contents[InventoryPos.of(row, column)]
    }

    override fun findFirstItemWithType(type: Material): InteractiveItem? {
        var result: InteractiveItem? = null

        for (slot in 0..inventorySlotCount) {
            val currentPosition: InventoryPos = MathUtils.slotToPosition(slot, getColumns())
            if (contents[currentPosition] == null) continue
            if (contents[currentPosition]?.item == null) continue
            if (contents[currentPosition]?.item?.type !== type) continue
            result = contents[currentPosition]
        }

        return result
    }

    override fun createPagination(): InventoryPagination {
        if (this.pagination == null) this.pagination = InventoryPaginationImpl(this)
        return this.pagination!!
    }

    override fun updateRawInventory() {
        for ((position, value) in this.contents) {
            val interactiveItem: InteractiveItem = value ?: continue
            rawInventory?.setItem(MathUtils.positionToSlot(position, getColumns()), interactiveItem.item)
        }
    }

}
