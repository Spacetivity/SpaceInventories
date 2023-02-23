package net.spacetivity.template.singleplugin.translation

enum class TranslationKey(val tag: String, val type: TranslationType, val defaultText: String) {

    COMMAND_NO_PERMISSION(
        "player.command.noPermission",
        TranslationType.MESSAGE,
        "<prefix> <red>Du hast keine Berechtigung für diesen Befehl."
    ),

}