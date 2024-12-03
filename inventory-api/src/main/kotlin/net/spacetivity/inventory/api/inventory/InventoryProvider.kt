package net.spacetivity.inventory.api.inventory

import org.bukkit.entity.Player

interface InventoryProvider {

    fun init(player: Player, controller: net.spacetivity.inventory.api.inventory.InventoryController)

}
