package net.spacetivity.inventory.bukkit.api.inventory

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.metadata.FixedMetadataValue
import net.spacetivity.inventory.api.inventory.InventoryProvider
import net.spacetivity.inventory.api.inventory.SpaceInventory
import net.spacetivity.inventory.api.pagination.InventoryPagination
import net.spacetivity.inventory.bukkit.utils.SoundUtils
import net.spacetivity.inventory.bukkit.SpaceInventoryBukkit

class SpaceInventoryImpl(
    override val provider: InventoryProvider,
    override val title: Component,
    override val controller: net.spacetivity.inventory.api.inventory.InventoryController,
    override val isStaticInventory: Boolean,
) : SpaceInventory {

    private val pagination: InventoryPagination? = this.controller.pagination
    override val name: String = this.controller.getInventoryId()
    override val rows: Int = this.controller.getRows()
    override val columns: Int = this.controller.getColumns()
    override val isCloseable: Boolean = this.controller.isCloseable

    override fun open(holder: Player) {
        validateOpening(holder)
    }

    override fun open(holder: Player, pageId: Int) {
        if (validateOpening(holder)) return
        if (this.pagination != null) pagination.page(pageId)
    }

    override fun open(holder: Player, forceSyncOpening: Boolean) {
        Bukkit.getScheduler().runTask(SpaceInventoryBukkit.instance, Runnable { open(holder) })
    }

    override fun open(holder: Player, pageId: Int, forceSyncOpening: Boolean) {
        Bukkit.getScheduler().runTask(SpaceInventoryBukkit.instance, Runnable { open(holder, pageId) })
    }

    override fun close(holder: Player) {
        holder.closeInventory()
    }

    override fun close(holder: Player, forceSyncClosing: Boolean) {
        Bukkit.getScheduler().runTask(SpaceInventoryBukkit.instance, Runnable { close(holder) })
    }

    private fun validateOpening(holder: Player): Boolean {
        val permission = controller.properties.permission

        if (!permission.equals("", ignoreCase = true) && !holder.hasPermission(permission)) {
            holder.sendMessage(
                Component.text(
                    "You don't have the permission to open this inventory!",
                    NamedTextColor.RED
                )
            )
            return true
        }

        val rawInventory: Inventory = controller.rawInventory!!
        holder.openInventory(rawInventory)
        holder.setMetadata("open-inventory", FixedMetadataValue(SpaceInventoryBukkit.instance, this.name))
        if (this.controller.properties.playSoundOnClose) SoundUtils.playSound(holder, SoundUtils.OPEN)

        return false
    }
}
