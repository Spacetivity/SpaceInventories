package net.spacetivity.survival.core.region

import java.util.*

data class ClaimedRegion(
    val ownerId: UUID,
    var chunksClaimed: Int,
    var open: Boolean,
    val trustedPlayers: MutableList<UUID>
) {

    fun hasReachedClaimingLimit(): Boolean {
        return chunksClaimed >= 1 //TODO: Change this to configurable value
    }

}