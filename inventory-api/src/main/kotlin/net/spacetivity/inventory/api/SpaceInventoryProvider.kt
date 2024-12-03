package net.spacetivity.inventory.api

class SpaceInventoryProvider {

    companion object {
        fun register(api: net.spacetivity.inventory.api.InventoryApi) {
            net.spacetivity.inventory.api.SpaceInventoryProvider.Companion.api = api
        }

        @JvmStatic
        lateinit var api: net.spacetivity.inventory.api.InventoryApi
            private set
    }

}

