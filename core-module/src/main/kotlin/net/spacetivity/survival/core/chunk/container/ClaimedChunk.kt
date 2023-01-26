package net.spacetivity.survival.core.chunk.container

import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import java.util.*

data class ClaimedChunk(val ownerId: UUID, val world: World, val xCoordinate: Int, val zCoordinate: Int) {

    private val bukkitChunk: Chunk = world.getChunkAt(xCoordinate, zCoordinate)

    fun constructChunk(): Chunk {
        return bukkitChunk
    }

    fun unload() {
        bukkitChunk.unload()
    }

    fun load() {
        bukkitChunk.load()
    }

    fun isLoaded(): Boolean {
        return bukkitChunk.isLoaded
    }

    fun mapChunkCornerToLocation(yLevel: Double): Location {
        return Location(world, xCoordinate * 16.0, yLevel, zCoordinate * 16.0)
    }

}