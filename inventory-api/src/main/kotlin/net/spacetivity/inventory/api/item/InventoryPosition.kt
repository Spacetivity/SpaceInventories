package net.spacetivity.inventory.api.item

data class InventoryPosition(val row: Int, val column: Int) {

    companion object {
        fun of(row: Int, column: Int): InventoryPosition {
            return InventoryPosition(row, column)
        }
    }

}
