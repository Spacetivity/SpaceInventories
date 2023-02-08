package net.spacetivity.survival.core.chunk

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import net.spacetivity.survival.core.SpaceSurvivalPlugin
import net.spacetivity.survival.core.land.Land
import net.spacetivity.survival.core.land.LandManager
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class ChunkManager(val plugin: SpaceSurvivalPlugin) {

    val cachedClaimedChunks: Multimap<UUID, Pair<Int, Int>> = ArrayListMultimap.create()
    val table = ChunkStorage

    fun isPlayerInChunk(player: Player, chunk: Chunk): Boolean {
        return player.chunk.x == chunk.x && player.chunk.z == chunk.z
    }

    fun getHighestPointInChunk(chunk: Chunk): Location {
        val highestBlocks: MutableList<Location> = mutableListOf()

        for (x in 0..15) {
            for (z in 0..15) {
                val yCordOfHighestBlockInChunk = chunk.world.getHighestBlockYAt((chunk.x * 16) + x, (chunk.z * 16) + z)
                for (y in -64..yCordOfHighestBlockInChunk) highestBlocks.add(chunk.getBlock(x, y, z).location)
            }
        }

        val highestBlocksSortedByYLevel: MutableList<Location> =
            highestBlocks.sortedBy { location -> location.y }.reversed().toMutableList()

        return highestBlocksSortedByYLevel[0]
    }

    fun getChunksAroundChunk(chunk: Chunk, selectClaimedChunks: Boolean): MutableList<Chunk> {
        val world = chunk.world
        val chunksAroundChunk: MutableList<Chunk> = mutableListOf()
        val offset = -1..1

        for (x in offset) for (z in offset) {
            val currentChunk: Chunk = world.getChunkAt(chunk.x + x, chunk.z + z)

            if (currentChunk.x == chunk.x && currentChunk.z == chunk.z) continue
            if (!selectClaimedChunks && isChunkClaimed(currentChunk)) continue

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

    fun isInClaimedChunk(uniqueId: UUID): Boolean {
        val chunk = Bukkit.getPlayer(uniqueId)?.chunk
        return hasClaimedChunk(uniqueId, chunk!!)
    }

    fun hasClaimedChunk(uniqueId: UUID, chunk: Chunk): Boolean {
        return plugin.chunkManager.getChunkOwner(chunk) == uniqueId
    }

    fun getClaimedChunksByPlayer(ownerId: UUID): MutableList<Pair<Int, Int>> {
        return cachedClaimedChunks.get(ownerId).toMutableList()
    }

    fun loadClaimedChunksByPlayer(ownerId: UUID): MutableList<Pair<Int, Int>> {
        val coordinatesOfClaimedChunks: MutableList<Pair<Int, Int>> = mutableListOf()

        transaction {
            table.select { table.ownerId eq ownerId.toString() }.map { row ->
                val pair: Pair<Int, Int> = Pair(row[table.coordinateX], row[table.coordinateZ])
                coordinatesOfClaimedChunks.add(pair)
            }
        }

        return coordinatesOfClaimedChunks
    }

    fun getChunkOwner(chunk: Chunk): UUID? {
        var ownerId: UUID? = null

        for ((possibleOwnerId, coordinatePair) in cachedClaimedChunks.entries()) if (coordinatePair.first == chunk.x && coordinatePair.second == chunk.z)
            ownerId = possibleOwnerId

        return ownerId
    }

    fun claimChunk(uniqueId: UUID, chunk: Chunk, updateLand: Boolean): ClaimResult {
        if (hasClaimedChunk(uniqueId, chunk)) return ClaimResult.ALREADY_CLAIMED
        if (plugin.chunkManager.isChunkClaimed(chunk)) return ClaimResult.ALREADY_CLAIMED_BY_OTHER_PLAYER

        val region: Land? = plugin.landManager.getLand(uniqueId)
        if (region != null && region.hasReachedClaimingLimit()) return ClaimResult.REACHED_MAX_CLAIM_LIMIT

        plugin.chunkManager.registerChunk(uniqueId, chunk.x, chunk.z)

        val isOriginalChunk: Boolean = loadClaimedChunksByPlayer(uniqueId).isEmpty()

        transaction {
            table.insert {
                it[ownerId] = uniqueId.toString()
                it[coordinateX] = chunk.x
                it[coordinateZ] = chunk.z
                it[originalChunk] = isOriginalChunk
            }

            if (updateLand) {
                val regionTable = LandManager.LandStorage

                regionTable.update({ regionTable.ownerId eq uniqueId.toString() }) {
                    it[regionTable.chunksClaimed] = region!!.chunksClaimed + 1
                }

                region!!.chunksClaimed += 1
            }
        }

        return ClaimResult.SUCCESS
    }

    fun unclaimAllChunksFromPlayer(ownerId: UUID) {
        unregisterRegisteredChunks(ownerId)
        transaction {
            ChunkStorage.deleteWhere { ChunkStorage.ownerId eq ownerId.toString() }
        }
    }

    fun loadClaimedChunks(ownerId: UUID) =
        loadClaimedChunksByPlayer(ownerId).forEach { coords -> registerChunk(ownerId, coords.first, coords.second) }

    fun registerChunk(ownerId: UUID, x: Int, z: Int) = cachedClaimedChunks.put(ownerId, Pair(x, z))
    fun unregisterChunk(ownerId: UUID, x: Int, z: Int) = cachedClaimedChunks.remove(ownerId, Pair(x, z))
    fun unregisterRegisteredChunks(ownerId: UUID) =
        cachedClaimedChunks.entries().filter { entry -> entry.key == ownerId }
            .forEach { (ownerId, coords) -> unregisterChunk(ownerId, coords.first, coords.second) }

    fun getOriginalChunk(ownerId: UUID): Triple<Int, Int, Boolean>? {
        var triple: Triple<Int, Int, Boolean>? = null

        transaction {

            ChunkStorage.select { ChunkStorage.ownerId eq ownerId.toString() and (ChunkStorage.originalChunk eq true) }.map { row ->
                val x = row[ChunkStorage.coordinateX]
                val z = row[ChunkStorage.coordinateZ]
                val isOriginalChunk = row[ChunkStorage.originalChunk]
                triple = Triple(x, z, isOriginalChunk)
            }

        }

        return triple
    }


    object ChunkStorage : Table("claimed_chunks") {
        val ownerId: Column<String> = varchar("ownerId", 50)
        val coordinateX: Column<Int> = integer("coordinateX")
        val coordinateZ: Column<Int> = integer("coordinateZ")
        val originalChunk: Column<Boolean> = bool("originalChunk")
    }
}