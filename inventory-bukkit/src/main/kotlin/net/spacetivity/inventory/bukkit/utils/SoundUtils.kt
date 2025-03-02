package net.spacetivity.inventory.bukkit.utils

import net.spacetivity.inventory.bukkit.SpaceInventoryBukkit
import org.bukkit.Sound
import org.bukkit.entity.Player

object SoundUtils {

    val CLICK: Sound? = SpaceInventoryBukkit.instance.soundConfigFile.onClick ?: Sound.BLOCK_NOTE_BLOCK_HAT
    val OPEN: Sound? = SpaceInventoryBukkit.instance.soundConfigFile.onOpen ?: Sound.BLOCK_COPPER_DOOR_OPEN
    val CLOSE: Sound? = SpaceInventoryBukkit.instance.soundConfigFile.onClose ?: Sound.BLOCK_COPPER_DOOR_CLOSE
    val PAGE_SWITCH: Sound? = SpaceInventoryBukkit.instance.soundConfigFile.onPageSwitch ?: Sound.BLOCK_COPPER_BULB_TURN_ON

    fun playClickSound(player: Player) {
        playSound(player, CLICK)
    }

    fun playOpenSound(player: Player) {
        playSound(player, OPEN)
    }

    fun playCloseSound(player: Player) {
        playSound(player, CLOSE)
    }

    fun playSwitchPageSound(player: Player) {
        playSound(player, PAGE_SWITCH)
    }

    private fun playSound(player: Player, sound: Sound?) {
        if (sound == null) return
        player.playSound(player.location, sound, 1f, 1f)
    }

}