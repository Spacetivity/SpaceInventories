package world.neptuns.inventory.api.utils

import world.neptuns.inventory.api.item.InventoryPos

object MathUtils {

    fun slotToPosition(slot: Int, columns: Int): InventoryPos {
        return InventoryPos.of(slot / columns, slot % columns)
    }

    fun positionToSlot(pos: InventoryPos, columns: Int): Int {
        return pos.row * columns + pos.column
    }

    fun nextPositionFromSlot(slot: Int, columns: Int): InventoryPos {
        return slotToPosition(slot + 1, columns)
    }

}
