package world.neptuns.inventory.api.inventory

import com.google.common.collect.Multimap
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

interface InventoryHandler {

    val inventories: Multimap<Player, SpaceInventory>

    fun openStaticInventory(holder: Player, title: Component, provider: InventoryProvider, forceSyncOpening: Boolean)

    fun cacheInventory(holder: Player, title: Component, provider: InventoryProvider)

    fun cacheInventory(
        holder: Player,
        title: Component,
        provider: InventoryProvider,
        staticInventory: Boolean
    ): SpaceInventory?

    fun updateCachedInventory(holder: Player, inventoryId: String)

    fun clearCachedInventories(holder: Player)

    fun removeCachedInventory(holder: Player, inventory: SpaceInventory)

    fun getInventory(holder: Player, name: String): SpaceInventory?

}
