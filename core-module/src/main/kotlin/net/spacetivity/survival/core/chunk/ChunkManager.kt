package net.spacetivity.survival.core.chunk

import org.bukkit.Chunk
import org.bukkit.Location
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class ChunkManager {

    fun getChunksAroundChunk(chunk: Chunk, bypassClaimedChunks: Boolean): MutableList<Chunk> {
        val world = chunk.world
        val chunksAroundChunk: MutableList<Chunk> = mutableListOf()
        val offset = -1..1

        for (x in offset) for (z in offset) {
            val currentChunk = world.getChunkAt(chunk.x + x, chunk.z + z)
            if (bypassClaimedChunks && isChunkClaimed(currentChunk)) continue
            chunksAroundChunk.add(currentChunk)
        }

        return chunksAroundChunk
    }

    fun isChunkClaimed(chunk: Chunk): Boolean {
        var isClaimed = false

        transaction {
            val table = ChunkPlayer.ChunkStorage
            isClaimed = table.select { table.coordinateX eq chunk.x and (table.coordinateZ eq chunk.z) }.count().toInt() > 0
        }

        return isClaimed
    }

    fun getChunkCenterLocation(yLevel: Double, chunk: Chunk): Location {
        return Location(chunk.world, (chunk.x shl 4).toDouble(), yLevel, (chunk.z shl 4).toDouble()).add(8.0, 0.0, 8.0)
    }
}