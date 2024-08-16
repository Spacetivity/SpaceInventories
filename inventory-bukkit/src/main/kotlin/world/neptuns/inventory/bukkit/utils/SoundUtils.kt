package world.neptuns.inventory.bukkit.utils

import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.Player

object SoundUtils {

    val CLICK = Sound.BLOCK_NOTE_BLOCK_HAT
    val OPEN = Sound.BLOCK_COPPER_DOOR_OPEN
    val CLOSE = Sound.BLOCK_COPPER_DOOR_CLOSE
    val PAGE_SWITCH = Sound.BLOCK_COPPER_BULB_TURN_ON

    fun playClickSound(player: Player) {
        playSound(player, CLICK)
    }

    fun playSwitchPageSound(player: Player) {
        player.playSound(player.location, PAGE_SWITCH, SoundCategory.HOSTILE, 1.0f, 0.5f)
    }

    fun playSound(player: Player, sound: Sound) {
        player.playSound(player.location, sound, 1f, 1f)
    }

}