package net.spacetivity.inventory.api.pagination

import com.google.common.collect.Multimap
import net.spacetivity.inventory.api.item.InteractiveItem

interface InventoryPagination {

    val positions: List<Any>
    val items: Multimap<Int, InteractiveItem>

    fun getLastPageId(): Int
    fun getPageAmount(): Int

    fun isFirstPage(): Boolean
    fun isLastPage(): Boolean

    fun page(pageId: Int)

    fun toFirstPage()
    fun toLastPage()

    fun toNextPage()
    fun toPreviousPage()

    fun setItemField(startRow: Int, startColumn: Int, endRow: Int, endColumn: Int)
    fun distributeItems(items: List<InteractiveItem>)
    fun limitItemsPerPage(amount: Int)
    fun refreshPage()

}
