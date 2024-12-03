package net.spacetivity.inventory.api.item

data class InventoryPos(val row: Int, val column: Int) {

    companion object {
        fun of(row: Int, column: Int): net.spacetivity.inventory.api.item.InventoryPos {
            return net.spacetivity.inventory.api.item.InventoryPos(row, column)
        }
    }

}
