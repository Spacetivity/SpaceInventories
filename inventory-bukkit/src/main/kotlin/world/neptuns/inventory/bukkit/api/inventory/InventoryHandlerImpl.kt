package world.neptuns.inventory.bukkit.api.inventory

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import world.neptuns.inventory.api.inventory.InventoryProperties
import world.neptuns.inventory.api.inventory.InventoryController
import world.neptuns.inventory.api.inventory.InventoryHandler
import world.neptuns.inventory.api.inventory.InventoryProvider
import world.neptuns.inventory.api.inventory.NeptunInventory
import world.neptuns.inventory.bukkit.NeptunInventoryBukkit

class InventoryHandlerImpl : InventoryHandler {

    override val inventories: Multimap<Player, NeptunInventory> = ArrayListMultimap.create()

    override fun openStaticInventory(holder: Player, title: Component, provider: InventoryProvider, forceSyncOpening: Boolean) {
        val inventory: NeptunInventory = cacheInventory(holder, title, provider, true) ?: return
        inventory.open(holder, forceSyncOpening)
    }

    override fun cacheInventory(holder: Player, title: Component, provider: InventoryProvider) {
        cacheInventory(holder, title, provider, false)
    }

    override fun cacheInventory(holder: Player, title: Component, provider: InventoryProvider, staticInventory: Boolean): NeptunInventory? {
        val permission = if (provider.javaClass.getAnnotation(InventoryProperties::class.java) == null) ""
        else provider.javaClass.getAnnotation(InventoryProperties::class.java).permission

        if (!permission.equals("", true) && !holder.hasPermission(permission)) return null

        val controller: InventoryController = InventoryControllerImpl(provider)
        val rawInventory = Bukkit.createInventory(holder, controller.getRows() * controller.getColumns(), title)

        controller.rawInventory = rawInventory
        controller.constructEmptyContent()

        provider.init(holder, controller)

        controller.updateRawInventory()

        val inventory: NeptunInventory = NeptunInventoryImpl(provider, title, controller, staticInventory)

        inventories.put(holder, inventory)
        return inventory
    }

    override fun updateCachedInventory(holder: Player, inventoryId: String) {
        val inventory: NeptunInventory = getInventory(holder, inventoryId) ?: return

        val title: Component = inventory.title
        val provider: InventoryProvider = inventory.controller.provider

        inventories[holder].removeIf { inv: NeptunInventory -> inv.name.equals(inventoryId, true) }
        cacheInventory(holder, title, provider)

        val elytraInventory = getInventory(holder, inventoryId) ?: return
        val key: String = elytraInventory.name

        if (!holder.hasMetadata("open-inventory")) return
        if (!key.equals(holder.getMetadata("open-inventory")[0].value() as String, true)) return

        elytraInventory.open(holder, true)
    }

    override fun clearCachedInventories(holder: Player) {
        inventories.removeAll(holder)
        if (holder.hasMetadata("open-inventory"))
            holder.removeMetadata("open-inventory", NeptunInventoryBukkit.instance)
    }

    override fun removeCachedInventory(holder: Player, inventory: NeptunInventory) {
        inventories.remove(holder, inventory)
        if (holder.hasMetadata("open-inventory")) {
            holder.removeMetadata("open-inventory", NeptunInventoryBukkit.instance)
        }
    }

    override fun getInventory(holder: Player, name: String): NeptunInventory? {
        return inventories[holder].first { it.name.equals(name, true) }
    }
}
