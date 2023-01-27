package net.spacetivity.survival.core.translation

data class TranslatableTextFile(val type: TranslationType, val translations: MutableList<TranslatableText> = mutableListOf())