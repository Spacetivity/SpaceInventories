package net.spacetivity.survival.core.inventory

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class InventoryManager {

    fun loadInventory(player: Player) {
        val isExist = InventoryStorage.select { InventoryStorage.uniqueId eq player.uniqueId.toString() }.empty()
        if (!isExist) return

        InventoryStorage.select { InventoryStorage.uniqueId eq player.uniqueId.toString() }.limit(1).firstOrNull()?.let { row ->
            val items: Array<ItemStack?> = ItemSerializer.deserializeItems(row[InventoryStorage.serializedInventory])
            player.inventory.contents = items
        }
    }

    fun saveInventory(player: Player) {
        val contents: Array<ItemStack?> = player.inventory.contents
        if (contents.isEmpty()) return

        val serializedItems = ItemSerializer.serializeItems(contents)!!

        transaction {
            val isExist = InventoryStorage.select { InventoryStorage.uniqueId eq player.uniqueId.toString() }.empty()
            if (isExist) {
                InventoryStorage.update({ InventoryStorage.uniqueId eq player.uniqueId.toString() }) {
                    it[serializedInventory] = serializedItems
                }
            } else {
                InventoryStorage.insert {
                    it[uniqueId] = player.uniqueId.toString()
                    it[serializedInventory] = serializedItems
                }
            }
        }

    }

    object InventoryStorage : Table("player_inventories") {
        val uniqueId: Column<String> = varchar("uniqueId", 50)
        val serializedInventory: Column<String> = text("serializedInventory")
    }

}