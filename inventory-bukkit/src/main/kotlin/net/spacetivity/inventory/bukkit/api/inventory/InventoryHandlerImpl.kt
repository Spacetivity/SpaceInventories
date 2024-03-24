package net.spacetivity.inventory.bukkit.api.inventory

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import net.kyori.adventure.text.Component
import net.spacetivity.inventory.api.annotation.InventoryProperties
import net.spacetivity.inventory.api.inventory.InventoryController
import net.spacetivity.inventory.api.inventory.InventoryHandler
import net.spacetivity.inventory.api.inventory.InventoryProvider
import net.spacetivity.inventory.api.inventory.SpaceInventory
import net.spacetivity.inventory.bukkit.SpaceInventoryBukkit
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class InventoryHandlerImpl : InventoryHandler {

    override val inventories: Multimap<Player, SpaceInventory> = ArrayListMultimap.create()

    override fun openStaticInventory(
        holder: Player,
        title: Component,
        provider: InventoryProvider,
        forceSyncOpening: Boolean
    ) {
        val inventory: SpaceInventory = cacheInventory(holder, title, provider, true) ?: return
        inventory.open(holder, forceSyncOpening)
    }

    override fun cacheInventory(holder: Player, title: Component, provider: InventoryProvider) {
        cacheInventory(holder, title, provider, false)
    }

    override fun cacheInventory(
        holder: Player,
        title: Component,
        provider: InventoryProvider,
        staticInventory: Boolean
    ): SpaceInventory? {
        val permission = if (provider.javaClass.getAnnotation(InventoryProperties::class.java) == null) ""
        else provider.javaClass.getAnnotation(InventoryProperties::class.java).permission

        if (!permission.equals("", true) && !holder.hasPermission(permission)) return null

        val controller: InventoryController = InventoryControllerImpl(provider)
        val invTitle: Component

//        if (provider is SpaceConfirmationInventory) {
//            controller.overriddenInventoryId = provider.inventoryId
//            controller.overriddenRows = 3
//            controller.overriddenColumns = 9
//            controller.isCloseable = true
//            invTitle = provider.title
//        } else {
//            invTitle = title
//        }

        invTitle = title

        val rawInventory = Bukkit.createInventory(holder, controller.getRows() * controller.getColumns(), invTitle)

        controller.rawInventory = rawInventory
        controller.constructEmptyContent()

        provider.init(holder, controller)

        controller.updateRawInventory()

        val inventory: SpaceInventory = SpaceInventoryImpl(provider, invTitle, controller, staticInventory)

        inventories.put(holder, inventory)
        return inventory
    }

    override fun updateCachedInventory(holder: Player, inventoryId: String) {
        val inventory: SpaceInventory = getInventory(holder, inventoryId) ?: return

        val title: Component = inventory.title
        val provider: InventoryProvider = inventory.controller.provider

        inventories[holder].removeIf { inv: SpaceInventory -> inv.name.equals(inventoryId, true) }
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
            holder.removeMetadata("open-inventory", SpaceInventoryBukkit.instance)
    }

    override fun removeCachedInventory(holder: Player, inventory: SpaceInventory) {
        inventories.remove(holder, inventory)
        if (holder.hasMetadata("open-inventory")) {
            holder.removeMetadata("open-inventory", SpaceInventoryBukkit.instance)
        }
    }

    override fun getInventory(holder: Player, name: String): SpaceInventory? {
        return inventories[holder].first { it.name.equals(name, true) }
    }
}
