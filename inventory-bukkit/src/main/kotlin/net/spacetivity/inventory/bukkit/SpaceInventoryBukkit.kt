package net.spacetivity.inventory.bukkit

import net.spacetivity.inventory.api.SpaceInventoryProvider
import net.spacetivity.inventory.bukkit.api.InventoryApiImpl
import net.spacetivity.inventory.bukkit.listener.InventoryPlayerListener
import org.bukkit.plugin.java.JavaPlugin

class SpaceInventoryBukkit : JavaPlugin() {

    override fun onEnable() {
        instance = this

        val inventoryApi = InventoryApiImpl()
        SpaceInventoryProvider.register(inventoryApi) //TODO: add sound profile for sound inv events

        InventoryPlayerListener(this)
    }

    companion object {
        @JvmStatic
        lateinit var instance: SpaceInventoryBukkit
            private set
    }

}
