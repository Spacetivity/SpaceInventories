package net.spacetivity.survival.core.translation

enum class TranslationKey(val tag: String, val type: TranslationType) {

    JOIN_MESSAGE("translation.player.join", TranslationType.MESSAGE),
    QUIT_MESSAGE("translation.player.quit", TranslationType.MESSAGE),

}