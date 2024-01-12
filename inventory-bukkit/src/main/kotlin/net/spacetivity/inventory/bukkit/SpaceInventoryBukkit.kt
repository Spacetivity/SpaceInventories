package net.spacetivity.inventory.bukkit

import net.spacetivity.inventory.api.InventoryApi
import net.spacetivity.inventory.api.SpaceInventoryProvider
import net.spacetivity.inventory.bukkit.api.InventoryApiImpl
import net.spacetivity.inventory.bukkit.listener.InventoryPlayerListener
import org.bukkit.plugin.java.JavaPlugin

class SpaceInventoryBukkit : JavaPlugin() {

    override fun onEnable() {
        instance = this

        val inventoryApi: InventoryApi = InventoryApiImpl()
        SpaceInventoryProvider.register(inventoryApi)

        InventoryPlayerListener(this)
    }

    companion object {
        @JvmStatic
        lateinit var instance: SpaceInventoryBukkit
            private set
    }

}
