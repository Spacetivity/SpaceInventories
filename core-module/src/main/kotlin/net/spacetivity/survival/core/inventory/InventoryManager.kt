package net.spacetivity.survival.core.inventory

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class InventoryManager {

    fun loadInventory(player: Player) {
        transaction {
            if (InventoryStorage.select { InventoryStorage.uniqueId eq player.uniqueId.toString() }.empty()) {
                saveInventory(player)
            } else {
                InventoryStorage.select { InventoryStorage.uniqueId eq player.uniqueId.toString() }.limit(1)
                    .firstOrNull()?.let { row ->
                    val items: Array<ItemStack?> =
                        ItemSerializer.deserializeItems(row[InventoryStorage.serializedInventory])
                    player.inventory.contents = items
                }
            }
        }
    }

    fun saveInventory(player: Player) {
        val contents: Array<ItemStack?> =
            player.inventory.contents.filter { itemStack -> itemStack != null && itemStack.type != Material.AIR }
                .toTypedArray()

        if (contents.isEmpty()) {
            transaction {
                InventoryStorage.deleteWhere { uniqueId eq player.uniqueId.toString() }
            }
            return
        }

        val serializedItems = ItemSerializer.serializeItems(contents)!!

        transaction {
            if (!InventoryStorage.select { InventoryStorage.uniqueId eq player.uniqueId.toString() }.empty()) {
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