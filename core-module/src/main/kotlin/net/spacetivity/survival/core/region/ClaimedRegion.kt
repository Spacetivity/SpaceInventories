package net.spacetivity.survival.core.region

import net.spacetivity.survival.core.location.MCLoc
import java.util.*

data class ClaimedRegion(
    val ownerId: UUID,
    var chunksClaimed: Int,
    var open: Boolean,
    val trustedPlayers: MutableList<UUID>,
    val locations: MutableList<MCLoc>
) {

    fun hasReachedClaimingLimit(): Boolean {
        return chunksClaimed >= 4 //TODO: Change this to configurable value
    }

}