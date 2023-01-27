package net.spacetivity.survival.core.translation

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags
import java.util.*

data class TranslatableText(val type: TranslationType, val key: TranslationKey, val text: String) {

    fun toComponent(toReplace: Array<TagResolver>): Component {
        val builder = MiniMessage.builder()
        val tagBuilder = TagResolver.builder()
            .resolver(StandardTags.gradient())
            .resolver(StandardTags.color())
            .resolver(StandardTags.decorations())
            .resolver(StandardTags.clickEvent())
            .resolver(StandardTags.hoverEvent())
            .resolvers(*toReplace)

        if (text.contains("<prefix")) tagBuilder.resolver(checkForPrefix()!!)

        return builder.tags(tagBuilder.build()).build().deserialize(text);
    }

    fun checkForPrefix(): TagResolver.Single? {
        var placeholder: TagResolver.Single? = null

        if (text.contains("<prefix_")) {
            val strings: Array<String> = text.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val splittedPrefixTag = strings[0].split("_".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            val unformattedPrefixName = splittedPrefixTag[1]
            val prefixName = unformattedPrefixName.substring(0, unformattedPrefixName.length - 1)
            val validPrefixName = prefixName[0].toString().uppercase(Locale.getDefault()) + prefixName.substring(1)
            val prefixFormat = "<dark_aqua><prefix_text> <gray>|" //TODO: implement the correct prefix!!
            val prefix = prefixFormat.replace("<prefix_text>".toRegex(), validPrefixName)
            placeholder = Placeholder.component(
                "prefix_$prefixName", MiniMessage.builder().tags(
                    TagResolver.builder()
                        .resolver(StandardTags.gradient())
                        .resolver(StandardTags.color())
                        .resolver(StandardTags.decorations())
                        .resolver(StandardTags.clickEvent())
                        .resolver(StandardTags.hoverEvent())
                        .build()
                ).build().deserialize(prefix)
            )
        } else if (text.contains("<prefix>")) {
            val prefix = "<dark_aqua>Network <gray>|" //TODO: implement the correct prefix!!
            val outputPrefix: String = prefix
            placeholder = Placeholder.component(
                "prefix", MiniMessage.builder().tags(
                    TagResolver.builder()
                        .resolver(StandardTags.gradient())
                        .resolver(StandardTags.color())
                        .resolver(StandardTags.decorations())
                        .resolver(StandardTags.clickEvent())
                        .resolver(StandardTags.hoverEvent())
                        .build()
                ).build().deserialize(outputPrefix)
            )
        }

        return placeholder
    }

}