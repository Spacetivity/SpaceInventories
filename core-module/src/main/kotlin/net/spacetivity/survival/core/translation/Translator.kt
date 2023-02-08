package net.spacetivity.survival.core.translation

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.spacetivity.survival.core.SpaceSurvivalPlugin
import org.bukkit.entity.Player

object Translator {

    private val cachedTranslations: MutableList<TranslatableText> =
        SpaceSurvivalPlugin.instance.translationManager.cachedTranslations

    fun findRawPrefix(globalPrefix: Boolean): String {
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

        if (cachedTranslations.none { text -> text.key == key.tag }) {
            player.sendMessage(Component.text("Message ${key.tag} not found...", NamedTextColor.RED))
            return
        }

        val translatableText: TranslatableText = cachedTranslations.filter { text -> text.key == key.tag }[0]

        val sendFunction: TextSendFunction = translatableText.sendFunction
        val toComponent = translatableText.toComponent(*toReplace)

        when (sendFunction) {
            TextSendFunction.CHAT -> player.sendMessage(toComponent)
            TextSendFunction.ACTION_BAR -> player.sendActionBar(toComponent)
        }

        if (translatableText.playSoundOnTrigger && translatableText.sound != null) {
            player.playSound(player.location, translatableText.sound, 1F, 1F)
        }
    }

    fun getTranslation(key: TranslationKey, vararg toReplace: TagResolver): Component {
        val translatableText: TranslatableText = cachedTranslations.filter { text -> text.key == key.tag }[0]
        return translatableText.toComponent(*toReplace)
    }

}