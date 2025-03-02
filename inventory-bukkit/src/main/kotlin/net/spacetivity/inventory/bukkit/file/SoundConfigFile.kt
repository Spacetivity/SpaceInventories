package net.spacetivity.inventory.bukkit.file

import org.bukkit.Sound

data class SoundConfigFile(
    val onClick: Sound? = null,
    val onOpen: Sound? = null,
    val onClose: Sound? = null,
    val onPageSwitch: Sound? = null,
) : SpaceFile