package net.spacetivity.survival.core.rank

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags

class RankFormat(val color: TextColor, val prefix: String, val tablistFormat: String, val chatFormat: String) {

    fun build(username: String, format: Format): Component {
        val messageBuilder: MiniMessage.Builder = MiniMessage.builder()
        val tagBuilder: TagResolver.Builder = TagResolver.builder()
            .resolvers(StandardTags.gradient(), StandardTags.color(), StandardTags.decorations())

        val formatPattern = if (format == Format.TABLIST) tablistFormat else chatFormat

        if (formatPattern.contains("<coloredname>") || formatPattern.contains("prefix"))
            tagBuilder.resolver(checkForColor(username, formatPattern))

        if (!formatPattern.contains("<coloredname>"))
            tagBuilder.resolver(Placeholder.parsed("username", username))

        return messageBuilder.tags(tagBuilder.build()).build().deserialize(formatPattern)
    }

    fun checkForColor(username: String, formatPattern: String): TagResolver.Single {
        val tagResolver: TagResolver = TagResolver.builder()
            .resolver(StandardTags.gradient())
            .resolver(StandardTags.color())
            .resolver(StandardTags.decorations())
            .build()

        return if (formatPattern.contains("<prefix")) {
            Placeholder.component(
                "prefix", MiniMessage.builder().tags(tagResolver).build()
                    .deserialize("<color:${color.asHexString()}>$prefix")
            )
        } else {
            Placeholder.component(
                "coloredname", MiniMessage.builder().tags(tagResolver).build()
                    .deserialize("<color:${color.asHexString()}>$username")
            )
        }
    }

    enum class Format {
        TABLIST,
        CHAT
    }
}