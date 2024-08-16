package world.neptuns.inventory.api

class NeptunInventoryProvider {

    companion object {
        fun register(api: InventoryApi) {
            Companion.api = api
        }

        @JvmStatic
        lateinit var api: InventoryApi
            private set
    }

}

