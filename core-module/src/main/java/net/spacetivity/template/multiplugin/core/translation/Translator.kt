package net.spacetivity.template.singleplugin.translation

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player

class Translator(private val cachedTranslations: MutableList<TranslatableText>) {

    fun findRawPrefix(globalPrefix: Boolean): String {
        return if (cachedTranslations.none { text -> text.key == (if (globalPrefix) "prefix.global" else "prefix.individual") }) {
            "Prefix not found..."
        } else {
            val translatableText: TranslatableText =
                cachedTranslations.filter { text -> text.key == (if (globalPrefix) "prefix.global" else "prefix.individual") }[0]
            translatableText.text
        }
    }

    fun sendMessage(player: Player, key: TranslationKey, vararg toReplace: TagResolver) {
        if (cachedTranslations.none { text -> text.key == key.tag }) {
            player.sendMessage(Component.text("Message ${key.tag} not found...", NamedTextColor.RED))
            return
        }

        val translatableText: TranslatableText = cachedTranslations.filter { text -> text.key == key.tag }[0]
        val sendFunction: TextSendFunction = translatableText.sendFunction
        val componentList: MutableList<Component> = translatableText.toMultiComponent(*toReplace)

        when (sendFunction) {
            TextSendFunction.CHAT -> for (component in componentList) player.sendMessage(component)
            TextSendFunction.ACTION_BAR -> player.sendActionBar(componentList[0])
        }
    }

    fun getTranslation(key: TranslationKey, vararg toReplace: TagResolver): Component {
        val translatableText: TranslatableText = cachedTranslations.filter { text -> text.key == key.tag }[0]
        return translatableText.toComponent(*toReplace)
    }

    fun getTranslationAsString(key: TranslationKey, vararg toReplace: TagResolver): String {
        val translatableText: TranslatableText = cachedTranslations.filter { text -> text.key == key.tag }[0]
        return PlainTextComponentSerializer.plainText().serialize(translatableText.toComponent(*toReplace))
    }

}