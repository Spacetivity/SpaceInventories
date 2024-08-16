package world.neptuns.inventory.bukkit

import world.neptuns.inventory.api.InventoryApi
import world.neptuns.inventory.api.NeptunInventoryProvider
import org.bukkit.plugin.java.JavaPlugin
import world.neptuns.inventory.bukkit.api.InventoryApiImpl
import world.neptuns.inventory.bukkit.listener.InventoryPlayerListener

class NeptunInventoryBukkit : JavaPlugin() {

    override fun onEnable() {
        instance = this

        val inventoryApi: InventoryApi = InventoryApiImpl()
        NeptunInventoryProvider.register(inventoryApi)

        InventoryPlayerListener(this)
    }

    companion object {
        @JvmStatic
        lateinit var instance: NeptunInventoryBukkit
            private set
    }

}
