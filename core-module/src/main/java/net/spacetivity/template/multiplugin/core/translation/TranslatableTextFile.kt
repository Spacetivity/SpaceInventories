package net.spacetivity.template.singleplugin.translation

data class TranslatableTextFile(val type: TranslationType, val translations: MutableList<TranslatableText> = mutableListOf())