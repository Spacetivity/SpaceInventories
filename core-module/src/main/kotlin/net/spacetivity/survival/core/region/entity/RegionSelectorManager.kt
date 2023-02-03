package net.spacetivity.survival.core.region.entity

import org.bukkit.Chunk
import org.bukkit.entity.Player

class RegionSelectorManager {

    private val entities: MutableList<RegionSelector> = mutableListOf()

    fun registerSelector(selector: RegionSelector) = entities.add(selector)

    fun removeSelectorsFromPlayer(player: Player) {
        for (selectorEntity in getSelectorsFromPlayer(player)) selectorEntity.entity.remove()
        entities.removeAll { e -> e.owner.uniqueId == player.uniqueId }
    }

    fun getSelectorsFromPlayer(player: Player): List<RegionSelector> =
        entities.filter { e -> e.owner.uniqueId == player.uniqueId }

    fun getActiveSelector(player: Player): RegionSelector? =
        entities.find { e -> e.owner.uniqueId == player.uniqueId && e.isSelected }


    fun getSelector(player: Player, chunk: Chunk): RegionSelector? =
        entities.find { e -> e.owner.uniqueId == player.uniqueId && e.chunk.x == chunk.x && e.chunk.z == chunk.z }

}