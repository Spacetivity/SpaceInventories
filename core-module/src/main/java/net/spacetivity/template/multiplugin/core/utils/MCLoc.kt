package net.spacetivity.template.multiplugin.core.utils

import org.bukkit.Bukkit
import org.bukkit.Location

class MCLoc(
    val worldName: String,
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float,
    val pitch: Float
) {

    fun toBukkit(): Location {
        return Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch)
    }

    companion object {
        fun fromBukkit(location: Location): MCLoc {
            return MCLoc(location.world.name, location.x, location.y, location.z, location.yaw, location.pitch)
        }
    }

}