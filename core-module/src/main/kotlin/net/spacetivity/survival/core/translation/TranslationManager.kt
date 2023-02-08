package net.spacetivity.survival.core.translation

import net.spacetivity.survival.core.SpaceSurvivalPlugin
import org.bukkit.Sound
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class TranslationManager(val plugin: SpaceSurvivalPlugin) {

    val translationDirectory = File("${plugin.dataFolder.toPath()}/translation")
    val cachedTranslations: MutableList<TranslatableText> = mutableListOf()
    val textFiles: MutableMap<TranslationType, TranslatableTextFile> = mutableMapOf(
        Pair(TranslationType.MESSAGE, TranslatableTextFile(TranslationType.MESSAGE)),
        Pair(TranslationType.SCORE_BOARD, TranslatableTextFile(TranslationType.SCORE_BOARD)),
        Pair(TranslationType.ITEM_COMPONENT, TranslatableTextFile(TranslationType.ITEM_COMPONENT)),
        Pair(TranslationType.INVENTORY_COMPONENT, TranslatableTextFile(TranslationType.INVENTORY_COMPONENT)),
    )

    init {
        if (!Files.exists(translationDirectory.toPath())) Files.createDirectories(translationDirectory.toPath())
        defaultInitializeTranslationFiles()
    }

    fun defaultInitializeTranslationFiles() {
        for (dataFile: TranslatableTextFile in textFiles.values) {
            val file: File = Paths.get("${translationDirectory.path}/${dataFile.type.fileName}.json").toFile()

            // looks if the file exists
            if (!Files.exists(file.toPath())) {
                val messageFile: TranslatableTextFile = textFiles[TranslationType.MESSAGE]!!
                messageFile.translations.add(TranslatableText(plugin, "prefix.global", TextSendFunction.CHAT ,false, null,"<dark_aqua>Network <gray>|"))
                messageFile.translations.add(TranslatableText(plugin, "prefix.individual", TextSendFunction.CHAT ,false, null,"<dark_aqua><prefix_text> <gray>|"))

                // then all message keys for the selected type are collected and then for all key a default text is inserted in the correct file
                TranslationKey.values().filter { key -> key.type == dataFile.type }.forEach { key ->
                    val isMessageFile = key.type == TranslationType.MESSAGE
                    dataFile.translations.add(TranslatableText(plugin, key.tag, TextSendFunction.CHAT,
                        false, if(isMessageFile) Sound.ENTITY_PLAYER_LEVELUP else null, key.defaultText))
                }

                cachedTranslations.addAll(messageFile.translations)

                // then the file is saves in json
                plugin.fileUtils.saveFile(file, dataFile)
            } else {
                val translatableTextFile: TranslatableTextFile = plugin.fileUtils.readFile(file, TranslatableTextFile::class.java)!!
                cachedTranslations.addAll(translatableTextFile.translations)
            }

        }

        // then all the text files are scanned for their content and the existing content is cached
        val listOfTexts = textFiles.values.map { textFile -> textFile.translations }.toList()
        listOfTexts.forEach { translatableTexts -> cachedTranslations.addAll(translatableTexts) }
    }
}