package net.spacetivity.survival.core.utils

import net.spacetivity.survival.core.SpaceSurvivalPlugin
import net.spacetivity.survival.core.chunk.ChunkManager
import org.bukkit.*
import org.bukkit.entity.Player

object LandUtils {

    private val chunkManager: ChunkManager = SpaceSurvivalPlugin.instance.chunkManager

    fun showClaimedChunks(player: Player, yLevel: Int) {
        val world = player.world

        val dustOptions: Particle.DustOptions = Particle.DustOptions(
            Color.fromBGR(232, 183, 35),
            1F
        )

        chunkManager.getClaimedChunksByPlayer(player.uniqueId).forEach { pair ->
            val chunkX: Int = pair.first
            val chunkZ: Int = pair.second
            val minX: Int = chunkX * 16
            val minZ: Int = chunkZ * 16

            val north: Chunk = world.getChunkAt(chunkX, chunkZ -1)

            if (!chunkManager.getClaimedChunksByPlayer(player.uniqueId).contains(Pair(north.x, north.z))) {

                for (x in minX .. minX + 16) {

                    if (player.inventory.itemInMainHand.type == Material.GOLD_BLOCK) {
                        val location = Location(player.world, x.toDouble(), yLevel.toDouble(), minZ.toDouble())
                        player.teleport(location)
                    }

                    //player.spawnParticle(Particle.REDSTONE, x.toDouble(), yLevel.toDouble(), minZ.toDouble(), 20, dustOptions)

                    player.spawnParticle(Particle.BLOCK_MARKER, x.toDouble(), yLevel.toDouble(), minZ.toDouble(), 1,
                        Bukkit.createBlockData(Material.BARRIER))

                }

            }

            val south: Chunk = world.getChunkAt(chunkX, chunkZ + 1)

            if (!chunkManager.getClaimedChunksByPlayer(player.uniqueId).contains(Pair(south.x, south.z))) {

                for (x in minX .. minX + 16) {
                    player.spawnParticle(Particle.BLOCK_MARKER, x.toDouble(), yLevel.toDouble(), minZ + 16.0, 1,
                        Bukkit.createBlockData(Material.BARRIER))
                }

            }

            val west: Chunk = world.getChunkAt(chunkX - 1, chunkZ)

            if (!chunkManager.getClaimedChunksByPlayer(player.uniqueId).contains(Pair(west.x, west.z))) {

                for (z in minZ .. minZ + 16) {

                    player.spawnParticle(Particle.BLOCK_MARKER, minX.toDouble(), yLevel.toDouble(), z.toDouble(), 1,
                        Bukkit.createBlockData(Material.BARRIER))

                }

            }

            val east: Chunk = world.getChunkAt(chunkX + 1, chunkZ)

            if (!chunkManager.getClaimedChunksByPlayer(player.uniqueId).contains(Pair(east.x, east.z))) {

                for (z in minZ .. minZ + 16) {

                    player.spawnParticle(Particle.BLOCK_MARKER, minX + 16.0, yLevel.toDouble(), z.toDouble(), 1,
                        Bukkit.createBlockData(Material.BARRIER))

                }

            }

        }

    }

}