package net.spacetivity.inventory.api.inventory

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

interface SpaceInventory {

    val provider: InventoryProvider

    val controller: InventoryController

    val name: String

    val title: Component

    val rows: Int

    val columns: Int

    val isCloseable: Boolean

    val isStaticInventory: Boolean

    fun open(holder: Player)
    fun open(holder: Player, pageId: Int)

    fun open(holder: Player, forceSyncOpening: Boolean)
    fun open(holder: Player, pageId: Int, forceSyncOpening: Boolean)

    fun close(holder: Player)
    fun close(holder: Player, forceSyncClosing: Boolean)

}
