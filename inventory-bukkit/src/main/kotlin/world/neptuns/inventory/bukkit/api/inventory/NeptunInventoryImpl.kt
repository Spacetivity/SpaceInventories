package world.neptuns.inventory.bukkit.api.inventory

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.metadata.FixedMetadataValue
import world.neptuns.inventory.api.inventory.InventoryController
import world.neptuns.inventory.api.inventory.InventoryProvider
import world.neptuns.inventory.api.inventory.NeptunInventory
import world.neptuns.inventory.api.pagination.InventoryPagination
import world.neptuns.inventory.bukkit.utils.SoundUtils
import world.neptuns.inventory.bukkit.NeptunInventoryBukkit

class NeptunInventoryImpl(
    override val provider: InventoryProvider,
    override val title: Component,
    override val controller: InventoryController,
    override val isStaticInventory: Boolean,
) : NeptunInventory {

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
        Bukkit.getScheduler().runTask(NeptunInventoryBukkit.instance, Runnable { open(holder) })
    }

    override fun open(holder: Player, pageId: Int, forceSyncOpening: Boolean) {
        Bukkit.getScheduler().runTask(NeptunInventoryBukkit.instance, Runnable { open(holder, pageId) })
    }

    override fun close(holder: Player) {
        holder.closeInventory()
    }

    override fun close(holder: Player, forceSyncClosing: Boolean) {
        Bukkit.getScheduler().runTask(NeptunInventoryBukkit.instance, Runnable { close(holder) })
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
        holder.setMetadata("open-inventory", FixedMetadataValue(NeptunInventoryBukkit.instance, this.name))
        if (this.controller.properties.playSoundOnClose) SoundUtils.playSound(holder, SoundUtils.OPEN)

        return false
    }
}
