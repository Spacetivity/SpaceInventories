package net.spacetivity.template.singleplugin.translation

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags
import net.spacetivity.template.multiplugin.core.CoreModuleBootstrap
import java.util.*

data class TranslatableText(
    val key: String,
    val sendFunction: TextSendFunction,
    val text: String
) {

    private val defaultTags: Array<TagResolver> = arrayOf(
        StandardTags.gradient(),
        StandardTags.color(),
        StandardTags.decorations(),
        StandardTags.clickEvent(),
        StandardTags.hoverEvent()
    )

    fun toMultiComponent(vararg toReplace: TagResolver): MutableList<Component> {
        if (!isMultilineMessage()) return mutableListOf(parseComponent(text, *toReplace))

        val components: MutableList<Component> = mutableListOf()

        text.split("\n").forEach { line ->
            val component: Component = parseComponent(line, *toReplace)
            components.add(component)
        }

        return components
    }

    fun toComponent(vararg toReplace: TagResolver): Component {
        return parseComponent(text, *toReplace)
    }

    private fun parseComponent(line: String, vararg toReplace: TagResolver): Component {
        val builder = MiniMessage.builder()
        val tagBuilder = TagResolver.builder()
            .resolvers(*defaultTags)
            .resolvers(*toReplace)

        if (line.contains("<prefix")) tagBuilder.resolver(checkForPrefix()!!)
        return builder.tags(tagBuilder.build()).build().deserialize(line);
    }

    private fun isMultilineMessage(): Boolean {
        return text.contains("\n") || text.contains("\r")
    }

    private fun checkForPrefix(): TagResolver.Single? {
        var placeholder: TagResolver.Single? = null
        if (text.contains("<prefix_")) {
            val strings: Array<String> = text.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val splittedPrefixTag = strings[0].split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val unformattedPrefixName = splittedPrefixTag[1]
            val prefixName = unformattedPrefixName.substring(0, unformattedPrefixName.length - 1)
            val validPrefixName = prefixName[0].toString().uppercase(Locale.getDefault()) + prefixName.substring(1)
            val prefixFormat = CoreModuleBootstrap.instance.translator.findRawPrefix(false)
            val prefix = prefixFormat.replace("<prefix_text>".toRegex(), validPrefixName)
            placeholder = Placeholder.component(
                "prefix_$prefixName", MiniMessage.builder().tags(
                    TagResolver.builder().resolvers(*defaultTags).build()
                ).build().deserialize(prefix)
            )
        } else if (text.contains("<prefix>")) {
            val prefix = CoreModuleBootstrap.instance.translator.findRawPrefix(true)
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