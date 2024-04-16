package world.neptuns.inventory.api.inventory

import org.bukkit.entity.Player

interface InventoryProvider {

    fun init(player: Player, controller: InventoryController)

}
