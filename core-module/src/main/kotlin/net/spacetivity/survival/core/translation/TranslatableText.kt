package net.spacetivity.survival.core.translation

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags
import net.spacetivity.survival.core.SpaceSurvivalPlugin
import org.bukkit.Sound
import java.util.*

data class TranslatableText(
    val plugin: SpaceSurvivalPlugin,
    val key: String,
    val sendFunction: TextSendFunction,
    val playSoundOnTrigger: Boolean,
    val sound: Sound?,
    val text: String
) {

    private val defaultTags: Array<TagResolver> = arrayOf(
        StandardTags.gradient(),
        StandardTags.color(),
        StandardTags.decorations(),
        StandardTags.clickEvent(),
        StandardTags.hoverEvent()
    )

    fun toComponent(vararg toReplace: TagResolver): Component {
        val builder = MiniMessage.builder()
        val tagBuilder = TagResolver.builder()
            .resolvers(*defaultTags)
            .resolvers(*toReplace)

        if (text.contains("<prefix")) tagBuilder.resolver(checkForPrefix()!!)
        return builder.tags(tagBuilder.build()).build().deserialize(text);
    }

    fun checkForPrefix(): TagResolver.Single? {
        var placeholder: TagResolver.Single? = null
        if (text.contains("<prefix_")) {
            val strings: Array<String> = text.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val splittedPrefixTag = strings[0].split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val unformattedPrefixName = splittedPrefixTag[1]
            val prefixName = unformattedPrefixName.substring(0, unformattedPrefixName.length - 1)
            val validPrefixName = prefixName[0].toString().uppercase(Locale.getDefault()) + prefixName.substring(1)
            val prefixFormat = Translator.findRawPrefix(false)
            val prefix = prefixFormat.replace("<prefix_text>".toRegex(), validPrefixName)
            placeholder = Placeholder.component(
                "prefix_$prefixName", MiniMessage.builder().tags(
                    TagResolver.builder().resolvers(*defaultTags).build()
                ).build().deserialize(prefix)
            )
        } else if (text.contains("<prefix>")) {
            val prefix = Translator.findRawPrefix(true)
            val outputPrefix: String = prefix
            placeholder = Placeholder.component(
                "prefix", MiniMessage.builder().tags(
                    TagResolver.builder()
                        .resolvers(*defaultTags)
                        .build()
                ).build().deserialize(outputPrefix)
            )
        }

        return placeholder
    }

}