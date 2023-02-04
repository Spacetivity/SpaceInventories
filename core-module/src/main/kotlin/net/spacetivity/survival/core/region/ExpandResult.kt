package net.spacetivity.survival.core.region

enum class ExpandResult(val isSuccess: Boolean) {

    SUCCESS(true),
    ALREADY_IN_CLAIMING_PROCESS(false),
    REACHED_MAX_CLAIM_LIMIT(false),
    IS_NOT_IN_REGION(false),

}