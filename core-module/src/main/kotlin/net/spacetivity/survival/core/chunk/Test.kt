package net.spacetivity.survival.core.chunk

import org.bukkit.Chunk

class Test {
    fun test(chunk: Chunk) {
        for (x in 0..15) {
            for (z in 0..15) {
                val hy = chunk.world.getHighestBlockYAt(chunk.x * 16 + x, chunk.z * 16 + z)
                for (y in -64 until hy) {
                    val b = chunk.getBlock(x, y, z)
                }
            }
        }
    }
}