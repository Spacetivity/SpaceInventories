package world.neptuns.inventory.bukkit

import world.neptuns.inventory.api.InventoryApi
import world.neptuns.inventory.api.SpaceInventoryProvider
import org.bukkit.plugin.java.JavaPlugin
import world.neptuns.inventory.bukkit.api.InventoryApiImpl
import world.neptuns.inventory.bukkit.listener.InventoryPlayerListener

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
