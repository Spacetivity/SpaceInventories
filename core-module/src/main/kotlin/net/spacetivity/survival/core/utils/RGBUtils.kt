package net.spacetivity.survival.core.utils

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor

object RGBUtils {

    fun toRGBCode(color: NamedTextColor): Triple<Int, Int, Int> {
        val textColor = TextColor.color(color.asHSV())
        return Triple(textColor.red(), textColor.green(), textColor.blue())
    }

    fun toRGBCode(color: TextColor): Triple<Int, Int, Int> {
        return Triple(color.red(), color.green(), color.blue())
    }

}