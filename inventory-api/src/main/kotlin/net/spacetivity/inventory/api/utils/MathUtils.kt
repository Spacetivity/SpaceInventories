package net.spacetivity.inventory.api.utils

import net.spacetivity.inventory.api.item.InventoryPos

object MathUtils {

    fun slotToPosition(slot: Int, columns: Int): net.spacetivity.inventory.api.item.InventoryPos {
        return net.spacetivity.inventory.api.item.InventoryPos.of(slot / columns, slot % columns)
    }

    fun positionToSlot(pos: net.spacetivity.inventory.api.item.InventoryPos, columns: Int): Int {
        return pos.row * columns + pos.column
    }

    fun nextPositionFromSlot(slot: Int, columns: Int): net.spacetivity.inventory.api.item.InventoryPos {
        return slotToPosition(slot + 1, columns)
    }

}
