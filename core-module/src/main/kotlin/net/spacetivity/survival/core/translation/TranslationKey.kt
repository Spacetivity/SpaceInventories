package net.spacetivity.survival.core.translation

enum class TranslationKey(val tag: String, val type: TranslationType, val defaultText: String) {

    JOIN_MESSAGE("player.join", TranslationType.MESSAGE, "<dark_purple>Player <light_purple><name> <dark_purple>has joined the server."),
    QUIT_MESSAGE("player.quit", TranslationType.MESSAGE, "<dark_purple>Player <light_purple><name> <dark_purple>has left the server."),

    CLAIM_ITEM_NAME("item.claimItem.displayName", TranslationType.ITEM_COMPONENT, "<dark_purple>Claim your first chunk")

}