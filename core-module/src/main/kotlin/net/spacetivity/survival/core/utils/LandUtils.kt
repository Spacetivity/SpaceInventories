package net.spacetivity.survival.core.utils

import net.spacetivity.survival.core.SpaceSurvivalPlugin
import net.spacetivity.survival.core.chunk.ChunkManager
import org.bukkit.*
import org.bukkit.entity.Player

object LandUtils {

    private val chunkManager: ChunkManager = SpaceSurvivalPlugin.instance.chunkManager

    fun showClaimedChunks(player: Player, yLevel: Int) {
        val world = player.world

        chunkManager.getClaimedChunksByPlayer(player.uniqueId).forEach { pair ->
            val chunkX: Int = pair.first
            val chunkZ: Int = pair.second

            val north: Chunk = world.getChunkAt(chunkX, chunkZ - 1)
            val south: Chunk = world.getChunkAt(chunkX, chunkZ + 1)
            val east: Chunk = world.getChunkAt(chunkX + 1, chunkZ)
            val west: Chunk = world.getChunkAt(chunkX - 1, chunkZ)

            val claimedChunks: MutableList<Pair<Int, Int>> = chunkManager.getClaimedChunksByPlayer(player.uniqueId)

            if (!claimedChunks.contains(Pair(north.x, north.z))) {
                showChunkBorder(player, Pair(chunkX, chunkZ), ChunkFacing.NORTH, yLevel, true, Material.BARRIER.name)
            }

            if (!claimedChunks.contains(Pair(south.x, south.z))) {
                showChunkBorder(player, Pair(chunkX, chunkZ), ChunkFacing.SOUTH, yLevel, true, Material.BARRIER.name)
            }

            if (!claimedChunks.contains(Pair(east.x, east.z))) {
                showChunkBorder(player, Pair(chunkX, chunkZ), ChunkFacing.EAST, yLevel, true, Material.BARRIER.name)
            }

            if (!claimedChunks.contains(Pair(west.x, west.z))) {
                showChunkBorder(player, Pair(chunkX, chunkZ), ChunkFacing.WEST, yLevel, true, Material.BARRIER.name)
            }

        }
    }

    fun showChunkBorder(player: Player, chunk: Pair<Int, Int>, facing: ChunkFacing, yLevel: Int, isParticle: Boolean, data: String
    ) {
        val chunkX: Int = chunk.first
        val chunkZ: Int = chunk.second

        val minX: Int = chunkX * 16
        val minZ: Int = chunkZ * 16

        when (facing) {
            ChunkFacing.NORTH -> for (x in minX..minX + 16) {
                spawnBorderEntity(player, x.toDouble(), yLevel.toDouble(), minZ.toDouble(), isParticle, data)
            }

            ChunkFacing.SOUTH -> for (x in minX..minX + 16) {
                spawnBorderEntity(player, x.toDouble(), yLevel.toDouble(), minZ + 16.0, isParticle, data)
            }

            ChunkFacing.EAST -> for (z in minZ..minZ + 16) {
                spawnBorderEntity(player, minX + 16.0, yLevel.toDouble(), z.toDouble(), isParticle, data)
            }

            ChunkFacing.WEST -> for (z in minZ..minZ + 16) {
                spawnBorderEntity(player, minX.toDouble(), yLevel.toDouble(), z.toDouble(), isParticle, data)
            }
        }

    }

    private fun spawnBorderEntity(player: Player, x: Double, y: Double, z: Double, isParticle: Boolean, data: String) {
        val borderMaterial: Material = Material.valueOf(data)

        if (isParticle) {
            player.spawnParticle(Particle.BLOCK_MARKER, x, y, z, 1, Bukkit.createBlockData(borderMaterial))
        } else {
            Location(player.world, x, y, z).block.type = borderMaterial
        }
    }

    enum class ChunkFacing {
        NORTH,
        SOUTH,
        EAST,
        WEST
    }
}