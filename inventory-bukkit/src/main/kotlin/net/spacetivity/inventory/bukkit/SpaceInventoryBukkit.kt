package net.spacetivity.inventory.bukkit

import net.spacetivity.inventory.bukkit.api.InventoryApiImpl
import net.spacetivity.inventory.bukkit.listener.InventoryPlayerListener
import org.bukkit.plugin.java.JavaPlugin

class SpaceInventoryBukkit : JavaPlugin() {

    override fun onEnable() {
        instance = this

        val inventoryApi: net.spacetivity.inventory.api.InventoryApi = InventoryApiImpl()
        net.spacetivity.inventory.api.SpaceInventoryProvider.register(inventoryApi)

        InventoryPlayerListener(this)
    }

    companion object {
        @JvmStatic
        lateinit var instance: SpaceInventoryBukkit
            private set
    }

}
