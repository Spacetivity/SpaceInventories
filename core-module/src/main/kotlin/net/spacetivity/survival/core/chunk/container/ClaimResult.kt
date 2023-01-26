package net.spacetivity.survival.core.chunk.container

enum class ClaimResult(val isSuccess: Boolean) {

    SUCCESS(true),
    ALREADY_CLAIMED(false),
    ALREADY_CLAIMED_BY_OTHER_PLAYER(false)

}