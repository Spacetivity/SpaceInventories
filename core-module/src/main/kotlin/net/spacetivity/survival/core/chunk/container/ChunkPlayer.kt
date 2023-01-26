package net.spacetivity.survival.core.chunk.container

import net.spacetivity.survival.core.SpaceSurvivalPlugin
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

data class ChunkPlayer(
    val plugin: SpaceSurvivalPlugin,
    val uniqueId: UUID,
    val chunks: MutableList<ClaimedChunk> //TODO: change that to region
) {

    fun claimChunk(chunk: Chunk): ClaimResult {
        if (hasClaimedChunk(chunk)) return ClaimResult.ALREADY_CLAIMED
        if (plugin.chunkManager.isChunkClaimed(chunk)) return ClaimResult.ALREADY_CLAIMED_BY_OTHER_PLAYER

        // TODO: implement this when regions are added : chunks.add(ClaimedChunk(uniqueId, chunk.world, chunk.x, chunk.z))
        plugin.chunkManager.registerChunk(uniqueId, chunk.x, chunk.z)

        transaction {
            ChunkStorage.insert {
                it[ownerId] = uniqueId.toString()
                it[coordinateX] = chunk.x
                it[coordinateZ] = chunk.z
            }
        }
        return ClaimResult.SUCCESS
    }

    fun isInClaimedChunk(): Boolean {
        val chunk = Bukkit.getPlayer(uniqueId)?.chunk
        return hasClaimedChunk(chunk!!)
    }

    fun hasClaimedChunk(chunk: Chunk): Boolean {
        return plugin.chunkManager.getChunkOwner(chunk) == uniqueId
    }

    fun delete() {
        plugin.playerChunkManager.unregisterPlayer(uniqueId)
        transaction { ChunkStorage.deleteWhere { ownerId eq uniqueId.toString() } }
    }

    object ChunkStorage : Table("claimed_chunks") {
        val ownerId: Column<String> = varchar("ownerId", 50)
        val coordinateX: Column<Int> = integer("coordinateX")
        val coordinateZ: Column<Int> = integer("coordinateZ")
    }
}
