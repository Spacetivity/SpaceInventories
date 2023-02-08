package net.spacetivity.survival.core.inventory

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class InventoryManager {

    fun loadInventory(player: Player) {
        transaction {
            if (InventoryStorage.select { InventoryStorage.uniqueId eq player.uniqueId.toString() }.empty()) {
                saveInventory(player)
            } else {
                InventoryStorage.select { InventoryStorage.uniqueId eq player.uniqueId.toString() }.limit(1)
                    .firstOrNull()?.let { row ->
                        val data: Array<ItemStack?> = ItemSerializer.deserializePlayerInventory(
                            row[InventoryStorage.serializedInventory]
                        )

                        player.inventory.clear()
                        player.inventory.armorContents = emptyArray()
                        player.inventory.contents = data
                    }
            }
        }
    }

    fun saveInventory(player: Player) {
        val playerData: String? = ItemSerializer.serializePlayerInventory(player.inventory)

        transaction {
            if (!InventoryStorage.select { InventoryStorage.uniqueId eq player.uniqueId.toString() }.empty()) {
                InventoryStorage.update({ InventoryStorage.uniqueId eq player.uniqueId.toString() }) {
                    it[serializedInventory] = playerData!!
                }
            } else {
                InventoryStorage.insert {
                    it[uniqueId] = player.uniqueId.toString()
                    it[serializedInventory] = playerData!!
                }
            }
        }
    }

    object InventoryStorage : Table("player_inventories") {
        val uniqueId: Column<String> = varchar("uniqueId", 50)
        val serializedInventory: Column<String> = text("serializedInventory")
    }

}