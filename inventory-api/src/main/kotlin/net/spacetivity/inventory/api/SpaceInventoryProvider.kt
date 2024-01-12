package net.spacetivity.inventory.api

class SpaceInventoryProvider {

    companion object {
        fun register(api: InventoryApi) {
            SpaceInventoryProvider.api = api
        }

        @JvmStatic
        lateinit var api: InventoryApi
            private set
    }

}

