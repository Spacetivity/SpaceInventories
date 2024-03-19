package net.spacetivity.inventory.api.pagination

import com.google.common.collect.Multimap
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.spacetivity.inventory.api.item.InteractiveItem
import org.bukkit.NamespacedKey
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

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

    fun findPageItem(itemId: String): InteractiveItem? {
        var item: InteractiveItem? = null

        for (interactiveItem: InteractiveItem in this.items.entries().map { it.value }) {
            val itemMeta: ItemMeta = interactiveItem.item.itemMeta

            val namespacedKey = NamespacedKey("item", "itemid")
            if (!itemMeta.persistentDataContainer.has(namespacedKey, PersistentDataType.STRING)) continue

            println(0)

            val value: String = itemMeta.persistentDataContainer.getOrDefault(namespacedKey, PersistentDataType.STRING, "")
            if (value != itemId) continue

            item = interactiveItem
        }

        println("Found page item (${item == null}) ${if (item != null) PlainTextComponentSerializer.plainText().serialize(item.item.itemMeta.displayName()!!) else ""}")
        return item
    }

}
