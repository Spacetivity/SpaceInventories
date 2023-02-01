package net.spacetivity.survival.core.translation

enum class TranslationKey(val tag: String, val type: TranslationType, val defaultText: String) {

    JOIN_MESSAGE("player.join", TranslationType.MESSAGE, "<dark_purple>Player <light_purple><name> <dark_purple>has joined the server."),
    QUIT_MESSAGE("player.quit", TranslationType.MESSAGE, "<dark_purple>Player <light_purple><name> <dark_purple>has left the server."),

    CLAIM_ITEM_NAME("item.claimItem.displayName", TranslationType.ITEM_COMPONENT, "<dark_purple>Claim your first chunk"),

    REGION_CREATED("region.created", TranslationType.MESSAGE, "<green>You have claimed your first chunk. Now you have to process to expand your region."),

    PLAYER_NO_REGION_FOUND("region.noRegionFoundForPlayer", TranslationType.MESSAGE, "<red>You don't have claimed a region yet!"),

    PLAYER_REGION_UNCLAIMED("region.playerUnclaimedRegion", TranslationType.MESSAGE, "<green>You have successfully unclaimed your chunks."),

    PLAYER_REGION_EXPAND_PROCESS_NOT_MOVE("region.expander.notMove", TranslationType.MESSAGE, "red>You cannot move while in expandsion mode!"),

    PLAYER_REGION_CLAIM_REQUEST("region.expand.claimRequest", TranslationType.MESSAGE,
        "<green>Click <bold><click:run_command:/region addChunk <x> <z>><hover:show_text:'<gray>Claim the chunk'>HERE</click><!b> <green>to claim chunk: <gold>(<x> | <z>)"
    ),

}