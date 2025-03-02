package net.spacetivity.inventory.bukkit.file

data class MessageFile(
    val noPermissionMessage: String = "<red>You don't have the permission to open this inventory!",
) : SpaceFile
