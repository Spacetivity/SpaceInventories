package net.spacetivity.survival.core.chunk

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import net.spacetivity.survival.core.chunk.container.ChunkPlayer
import org.bukkit.Chunk
import org.bukkit.Location
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class ChunkManager {

    val cachedClaimedChunks: Multimap<UUID, Pair<Int, Int>> = ArrayListMultimap.create()
    val table = ChunkPlayer.ChunkStorage

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

    fun getChunkCenterLocation(yLevel: Double, chunk: Chunk): Location {
        return Location(chunk.world, (chunk.x shl 4).toDouble(), yLevel, (chunk.z shl 4).toDouble()).add(8.0, 0.0, 8.0)
    }

    fun isChunkClaimed(chunk: Chunk): Boolean {
        return cachedClaimedChunks.containsValue(Pair(chunk.x, chunk.z))
    }

    fun getChunkOwner(chunk: Chunk): UUID? {
        var ownerId: UUID? = null

        for ((possibleOwnerId, coordinatePair) in cachedClaimedChunks.entries()) if (coordinatePair.first == chunk.x && coordinatePair.second == chunk.z)
            ownerId = possibleOwnerId

        return ownerId
    }

    fun getClaimedChunksByPlayer(ownerId: UUID): MutableList<Pair<Int, Int>> {
        val coordinatesOfClaimedChunks: MutableList<Pair<Int, Int>> = mutableListOf()

        transaction {
            table.select { table.ownerId eq ownerId.toString() }.map { row ->
                val pair: Pair<Int, Int> = Pair(row[ChunkPlayer.ChunkStorage.coordinateX], row[ChunkPlayer.ChunkStorage.coordinateZ])
                coordinatesOfClaimedChunks.add(pair)
            }
        }

        return coordinatesOfClaimedChunks
    }

    fun registerChunk(ownerId: UUID, x: Int, z: Int) = cachedClaimedChunks.put(ownerId, Pair(x, z))
    fun unregisterChunk(ownerId: UUID, x: Int, z: Int) = cachedClaimedChunks.remove(ownerId, Pair(x, z))
    fun clearRegisteredChunks(ownerId: UUID): MutableCollection<Pair<Int, Int>> = cachedClaimedChunks.removeAll(ownerId)

}