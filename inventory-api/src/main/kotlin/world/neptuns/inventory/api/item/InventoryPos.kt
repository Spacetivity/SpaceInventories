package world.neptuns.inventory.api.item

data class InventoryPos(val row: Int, val column: Int) {

    companion object {
        fun of(row: Int, column: Int): InventoryPos {
            return InventoryPos(row, column)
        }
    }

}
