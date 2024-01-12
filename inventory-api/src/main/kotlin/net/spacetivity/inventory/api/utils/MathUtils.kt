package net.spacetivity.inventory.api.utils

import net.spacetivity.inventory.api.item.InventoryPosition

object MathUtils {

    fun slotToPosition(slot: Int, columns: Int): InventoryPosition {
        return InventoryPosition.of(slot / columns, slot % columns)
    }

    fun positionToSlot(pos: InventoryPosition, columns: Int): Int {
        return pos.row * columns + pos.column
    }

    fun nextPositionFromSlot(slot: Int, columns: Int): InventoryPosition {
        return slotToPosition(slot + 1, columns)
    }

}
