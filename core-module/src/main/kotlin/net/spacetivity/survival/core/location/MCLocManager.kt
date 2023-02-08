package net.spacetivity.survival.core.location

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class MCLocManager {

    val cachedLocations: MutableMap<String, MCLoc> = mutableMapOf()

    fun createLocation(player: Player, locationName: String, location: Location) {
        if (cachedLocations[locationName] != null) {
            player.sendMessage(
                Component.text("There is already a location named $locationName!").color(NamedTextColor.RED)
            )
            return
        }

        val newLocation =
            MCLoc(locationName, location.world.name, location.x, location.y, location.z, location.yaw, location.pitch)
        registerLocation(newLocation)

        transaction {
            MCLocStorage.insert {
                it[name] = locationName
                it[worldName] = location.world.name
                it[x] = location.x
                it[y] = location.y
                it[z] = location.z
                it[yaw] = location.yaw
                it[pitch] = location.pitch
            }
        }

    }

    fun registerLocation(newLocation: MCLoc) = cachedLocations.putIfAbsent(newLocation.name, newLocation)
    fun getRegion(name: String): MCLoc? = cachedLocations[name]

    object MCLocStorage : Table("locations") {
        val name: Column<String> = varchar("name", 50)
        val worldName: Column<String> = varchar("worldName", 50)
        val x: Column<Double> = double("x")
        val y: Column<Double> = double("y")
        val z: Column<Double> = double("z")
        val yaw: Column<Float> = float("yaw")
        val pitch: Column<Float> = float("pitch")
    }

}