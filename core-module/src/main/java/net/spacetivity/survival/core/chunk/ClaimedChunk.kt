package net.spacetivity.survival.core.chunk

import org.bukkit.Chunk
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

}