package net.spacetivity.survival.core.chunk

import org.bukkit.Chunk
import org.bukkit.Location
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class ChunkManager {

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