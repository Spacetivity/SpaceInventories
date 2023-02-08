package net.spacetivity.survival.core.location

import org.bukkit.Bukkit
import org.bukkit.Location

class MCLoc(
    val name: String,
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

    fun fromBukkit(name: String, location: Location): MCLoc {
        return MCLoc(name, location.world.name, location.x, location.y, location.z, location.yaw, location.pitch)
    }

}