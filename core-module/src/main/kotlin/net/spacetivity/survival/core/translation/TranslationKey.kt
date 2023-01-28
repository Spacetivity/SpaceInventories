package net.spacetivity.survival.core.translation

enum class TranslationKey(val tag: String, val type: TranslationType) {

    JOIN_MESSAGE("player.join", TranslationType.MESSAGE),
    QUIT_MESSAGE("player.quit", TranslationType.MESSAGE),
    CLAIM_ITEM_NAME("item.claimItem.displayName", TranslationType.ITEM_COMPONENT)

}