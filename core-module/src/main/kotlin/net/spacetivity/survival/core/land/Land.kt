package net.spacetivity.survival.core.land

import net.spacetivity.survival.core.location.MCLoc
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.util.*

data class Land(
    val ownerId: UUID,
    var chunksClaimed: Int,
    var open: Boolean,
    val trustedPlayers: MutableList<UUID>,
    val locations: MutableList<MCLoc>
) {

    fun getOwner(): OfflinePlayer {
        return Bukkit.getOfflinePlayer(ownerId)
    }

    fun hasReachedClaimingLimit(): Boolean {
        return chunksClaimed >= 4 //TODO: Change this to configurable value
    }

}