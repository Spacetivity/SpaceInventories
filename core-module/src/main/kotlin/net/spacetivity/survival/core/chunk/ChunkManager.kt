package net.spacetivity.survival.core.chunk

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import net.spacetivity.survival.core.SpaceSurvivalPlugin
import net.spacetivity.survival.core.region.ClaimedRegion
import net.spacetivity.survival.core.region.RegionManager
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class ChunkManager(val plugin: SpaceSurvivalPlugin) {

    val cachedClaimedChunks: Multimap<UUID, Pair<Int, Int>> = ArrayListMultimap.create()
    val table = ChunkStorage

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

    fun isInClaimedChunk(uniqueId: UUID): Boolean {
        val chunk = Bukkit.getPlayer(uniqueId)?.chunk
        return hasClaimedChunk(uniqueId, chunk!!)
    }

    fun hasClaimedChunk(uniqueId: UUID, chunk: Chunk): Boolean {
        return plugin.chunkManager.getChunkOwner(chunk) == uniqueId
    }

    fun getClaimedChunksByPlayer(ownerId: UUID): MutableList<Pair<Int, Int>> {
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

    fun claimChunk(uniqueId: UUID, chunk: Chunk, updateRegion: Boolean): ClaimResult {
        if (hasClaimedChunk(uniqueId, chunk)) return ClaimResult.ALREADY_CLAIMED
        if (plugin.chunkManager.isChunkClaimed(chunk)) return ClaimResult.ALREADY_CLAIMED_BY_OTHER_PLAYER

        val region: ClaimedRegion? = plugin.regionManager.getRegion(uniqueId)
        if (region != null && region.hasReachedClaimingLimit()) return ClaimResult.REACHED_MAX_CLAIM_LIMIT

        plugin.chunkManager.registerChunk(uniqueId, chunk.x, chunk.z)

        transaction {
            table.insert {
                it[ownerId] = uniqueId.toString()
                it[coordinateX] = chunk.x
                it[coordinateZ] = chunk.z
            }

            if (updateRegion) {
                val regionTable = RegionManager.RegionStorage
                regionTable.update({ regionTable.ownerId eq uniqueId.toString() }) {
                    it[regionTable.chunksClaimed] = region!!.chunksClaimed + 1
                }

                region!!.chunksClaimed += 1
            }
        }

        return ClaimResult.SUCCESS
    }

    fun loadClaimedChunks(ownerId: UUID) = getClaimedChunksByPlayer(ownerId).forEach { coords -> registerChunk(ownerId, coords.first, coords.second) }
    fun registerChunk(ownerId: UUID, x: Int, z: Int) = cachedClaimedChunks.put(ownerId, Pair(x, z))
    fun unregisterChunk(ownerId: UUID, x: Int, z: Int) = cachedClaimedChunks.remove(ownerId, Pair(x, z))
    fun unregisterRegisteredChunks(ownerId: UUID) = cachedClaimedChunks.entries().filter { entry -> entry.key == ownerId }
        .forEach { (ownerId, coords) -> unregisterChunk(ownerId, coords.first, coords.second) }

    object ChunkStorage : Table("claimed_chunks") {
        val ownerId: Column<String> = varchar("ownerId", 50)
        val coordinateX: Column<Int> = integer("coordinateX")
        val coordinateZ: Column<Int> = integer("coordinateZ")
    }
}