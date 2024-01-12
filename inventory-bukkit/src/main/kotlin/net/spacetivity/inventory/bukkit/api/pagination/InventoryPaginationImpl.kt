package net.spacetivity.inventory.bukkit.api.pagination

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import net.spacetivity.inventory.api.inventory.InventoryController
import net.spacetivity.inventory.api.item.InteractiveItem
import net.spacetivity.inventory.api.item.InventoryPosition
import net.spacetivity.inventory.api.pagination.InventoryPagination


class InventoryPaginationImpl(private val controller: InventoryController) : InventoryPagination {

    override val positions: MutableList<InventoryPosition> = mutableListOf()
    override val items: Multimap<Int, InteractiveItem> = ArrayListMultimap.create()

    var currentPageId: Int = 0
    var itemsPerPage: Int = 9


    override fun getLastPageId(): Int {
        val pageIds = items.keySet().stream().toList()
        return pageIds[pageIds.size - 1]
    }

    override fun getPageAmount(): Int {
        return items.keySet().stream().toList().size
    }

    override fun isFirstPage(): Boolean {
        return this.currentPageId == 0
    }

    override fun isLastPage(): Boolean {
        val pageIds = items.keySet().stream().toList()
        return this.currentPageId == pageIds[pageIds.size - 1]
    }

    override fun page(pageId: Int) {
        val pageIds = items.keySet().stream().toList()
        this.currentPageId = if ((pageIds.size < pageId || pageId < 0)) 0 else pageId
        refreshPage()
    }

    override fun toFirstPage() {
        this.currentPageId = 0
        refreshPage()
    }

    override fun toLastPage() {
        val pageIds = items.keySet().stream().toList()
        this.currentPageId = pageIds[pageIds.size - 1]
    }

    override fun toNextPage() {
        if (isLastPage()) return
        this.currentPageId += 1
        refreshPage()
    }

    override fun toPreviousPage() {
        if (isFirstPage()) return
        this.currentPageId -= 1
        refreshPage()
    }

    override fun setItemField(startRow: Int, startColumn: Int, endRow: Int, endColumn: Int) {
        val numRows = controller.getRows()!!
        val numColumns = controller.getColumns()!!

        for (row in startRow until numRows.coerceAtMost(endRow + 1)) {
            for (column in startColumn until numColumns.coerceAtMost(endColumn + 1)) {
                val position = InventoryPosition.of(row, column)
                positions.add(position)
            }
        }
    }


    override fun distributeItems(items: List<InteractiveItem>) {
        this.items.clear()
        var pageIndex = 0

        for (i in items.indices) {
            if (i % itemsPerPage == 0) pageIndex = i / itemsPerPage
            this.items.put(pageIndex, items[i])
        }

        refreshPage()
    }


    override fun limitItemsPerPage(amount: Int) {
        this.itemsPerPage = amount
    }

    override fun refreshPage() {
        for (currentPosition in this.positions) {
            if (!controller.isPositionTaken(currentPosition)) continue
            controller.clearPosition(currentPosition)
        }

        val itemsForNextPage: List<InteractiveItem> = items[currentPageId].stream().toList()
        var itemIndex = 0

        for (currentPosition in this.positions) {
            if (itemIndex >= itemsForNextPage.size) break
            if (controller.isPositionTaken(currentPosition)) continue
            controller.setItem(currentPosition, itemsForNextPage[itemIndex])
            itemIndex++
        }

        controller.updateRawInventory()
    }
}
