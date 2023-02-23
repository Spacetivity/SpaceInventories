package net.spacetivity.template.singleplugin.translation

import net.spacetivity.template.multiplugin.core.CoreModuleBootstrap
import net.spacetivity.template.singleplugin.utils.FileUtils
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class TranslationRepository(val cachedTranslations: MutableList<TranslatableText>, dataFolder: Path) {

    private val fileUtils: FileUtils = CoreModuleBootstrap.instance.fileUtils
    private val translationDirectory = File("${dataFolder}/translation")
    private val textFiles: MutableMap<TranslationType, TranslatableTextFile> = mutableMapOf(
        Pair(TranslationType.MESSAGE, TranslatableTextFile(TranslationType.MESSAGE)),
        Pair(TranslationType.BOOK, TranslatableTextFile(TranslationType.BOOK)),
    )

    init {
        if (!Files.exists(translationDirectory.toPath())) Files.createDirectories(translationDirectory.toPath())
        defaultInitializeTranslationFiles()
    }

    private fun defaultInitializeTranslationFiles() {
        for (dataFile: TranslatableTextFile in textFiles.values) {
            val file: File = Paths.get("${translationDirectory.path}/${dataFile.type.fileName}.json").toFile()

            // looks if the file exists
            if (!Files.exists(file.toPath())) {
                val messageFile: TranslatableTextFile = textFiles[TranslationType.MESSAGE]!!
                messageFile.translations.add(TranslatableText( "prefix.global", TextSendFunction.CHAT ,"<dark_aqua>BetaTool <gray>|"))
                messageFile.translations.add(TranslatableText( "prefix.individual", TextSendFunction.CHAT ,"<dark_aqua><prefix_text> <gray>|"))

                // then all message keys for the selected type are collected and then for all key a default text is inserted in the correct file
                TranslationKey.values().filter { key -> key.type == dataFile.type }.forEach { key ->
                    dataFile.translations.add(TranslatableText(key.tag, TextSendFunction.CHAT, key.defaultText))
                }

                cachedTranslations.addAll(messageFile.translations)

                // then the file is saves in json
                CoreModuleBootstrap.instance.fileUtils.saveFile(file, dataFile)
            } else {
                val translatableTextFile: TranslatableTextFile = fileUtils.readFile(file, TranslatableTextFile::class.java)!!
                cachedTranslations.addAll(translatableTextFile.translations)
            }

        }

        // then all the text files are scanned for their content and the existing content is cached
        val listOfTexts = textFiles.values.map { textFile -> textFile.translations }.toList()
        listOfTexts.forEach { translatableTexts -> cachedTranslations.addAll(translatableTexts) }
    }
}