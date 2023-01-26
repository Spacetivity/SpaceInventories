package net.spacetivity.survival.core.chunk

enum class ClaimResult(val isSuccess: Boolean) {

    SUCCESS(true),
    ALREADY_CLAIMED(false),
    ALREADY_CLAIMED_BY_OTHER_PLAYER(false),
    REACHED_MAX_CLAIM_LIMIT(false)

}