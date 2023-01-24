package net.spacetivity.survival.core.chunk

import org.bukkit.Chunk
import org.bukkit.Location
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class ChunkManager {

    fun isChunkNextToClaimedChunk(chunk: Chunk): Boolean {
        return false
    }

    fun getChunksAroundChunk(chunk: Chunk, bypassClaimedChunks: Boolean): MutableList<Chunk> {
        val offset: Array<Int> = arrayOf(-1, 0, 1)
        val world = chunk.world
        val chunksAroundChunk: MutableList<Chunk> = mutableListOf()

        for (x in offset) for (z in offset) {
            val currentChunk = world.getChunkAt(chunk.x + x, chunk.z + z)
            if (bypassClaimedChunks && isChunkClaimed(currentChunk)) continue
            chunksAroundChunk.add(currentChunk)
        }

        return chunksAroundChunk
    }

    fun getChunkCenterLocation(yLevel: Double, chunk: Chunk): Location {
        // center.y = (center.world.getHighestBlockYAt(center) + 1).toDouble()
        return Location(chunk.world, (chunk.x shl 4).toDouble(), yLevel, (chunk.z shl 4).toDouble()).add(8.0, 0.0, 8.0)
    }

    fun mapChunkCornerToLocation(yLevel: Double, chunk: Chunk): Location {
        return Location(chunk.world, chunk.x * 16.0, yLevel, chunk.z * 16.0)
    }

    fun isChunkClaimed(chunk: Chunk): Boolean {
        var isClaimed = false

        transaction {
            val table = ChunkPlayer.ChunkStorage
            isClaimed = table.select { table.coordinateX eq chunk.x and (table.coordinateZ eq chunk.z) }.count().toInt() > 0
        }

        return isClaimed
    }

}