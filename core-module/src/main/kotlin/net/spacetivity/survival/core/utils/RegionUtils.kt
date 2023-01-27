package net.spacetivity.survival.core.utils

import net.spacetivity.survival.core.SpaceSurvivalPlugin
import net.spacetivity.survival.core.region.ClaimedRegion
import org.bukkit.entity.Player

object RegionUtils {

    fun doIfPlayerContainsRegion(plugin: SpaceSurvivalPlugin, player: Player, result: (ClaimedRegion) -> (Unit)) {
        if (plugin.regionManager.isInRegion(player)) result.invoke(plugin.regionManager.getRegion(player.uniqueId)!!)
    }

}