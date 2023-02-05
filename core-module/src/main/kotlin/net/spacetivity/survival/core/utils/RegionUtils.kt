package net.spacetivity.survival.core.utils

import net.spacetivity.survival.core.SpaceSurvivalPlugin
import net.spacetivity.survival.core.land.Land
import org.bukkit.entity.Player

object RegionUtils {

    fun doIfPlayerContainsRegion(plugin: SpaceSurvivalPlugin, player: Player, result: (Land) -> (Unit)) {
        if (plugin.landManager.isInLand(player)) result.invoke(plugin.landManager.getLand(player.uniqueId)!!)
    }

}