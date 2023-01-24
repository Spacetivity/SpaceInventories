package net.spacetivity.survival.core.chunk

import java.util.*

class PlayerChunkManager {

    private val cachedChunkPlayers: MutableMap<UUID, ChunkPlayer> = mutableMapOf()

    fun registerPlayer(player: ChunkPlayer) = cachedChunkPlayers.putIfAbsent(player.uniqueId, player)
    fun unregisterPlayer(ownerId: UUID) = cachedChunkPlayers.remove(ownerId)
    fun getPlayer(ownerId: UUID): ChunkPlayer? = cachedChunkPlayers[ownerId]
    fun getPlayers(): MutableCollection<ChunkPlayer> = cachedChunkPlayers.values

}