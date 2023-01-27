package net.spacetivity.survival.core.translation

import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.spacetivity.survival.core.SpaceSurvivalPlugin
import org.bukkit.entity.Player

class Translator(val plugin: SpaceSurvivalPlugin) {

    fun findRawPrefix(globalPrefix: Boolean): String {
        val cachedTranslations: MutableList<TranslatableText> = plugin.translationManager.cachedTranslations

        val result: String =
            if (cachedTranslations.none { text -> text.key == (if (globalPrefix) "prefix.global" else "prefix.individual") }) {
                "Prefix not found..."
            } else {
                val translatableText: TranslatableText =
                    cachedTranslations.filter { text -> text.key == (if (globalPrefix) "prefix.global" else "prefix.individual") }[0]
                translatableText.text
            }

        return result
    }

    fun sendMessage(player: Player, key: TranslationKey, vararg toReplace: TagResolver) {
        val translatableText: TranslatableText = plugin.translationManager.cachedTranslations.filter { text -> text.key == key.tag }[0]

        println(translatableText.key)

        val sendFunction: TextSendFunction = translatableText.sendFunction
        val toComponent = translatableText.toComponent(*toReplace)

        println(1)

        when (sendFunction) {
            TextSendFunction.CHAT -> player.sendMessage(toComponent)
            TextSendFunction.ACTION_BAR -> player.sendActionBar(toComponent)
        }
    }

}