package net.spacetivity.survival.core.listener

import net.spacetivity.survival.core.SpaceSurvivalPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class ItemClickListener : Listener {

    @EventHandler
    fun onInteractWithClickableItem(event: PlayerInteractEvent) {
        if (event.item == null) return
        if (event.item?.itemMeta == null) return

        if (SpaceSurvivalPlugin.clickableItems.map { itemBuilder -> itemBuilder.itemStack }.contains(event.item!!))
            SpaceSurvivalPlugin.clickableItems.first { itemBuilder -> itemBuilder.itemStack == event.item!! }.action.invoke(event)
    }

}