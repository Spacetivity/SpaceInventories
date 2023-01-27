package net.spacetivity.survival.core.utils

import net.spacetivity.survival.core.SpaceSurvivalPlugin
import java.io.*

class FileUtils(val plugin: SpaceSurvivalPlugin) {

    fun <T> readFile(file: File, clazz: Class<T>): T? {
        return try {
            plugin.gson.fromJson(FileReader(file), clazz)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    fun saveFile(file: File, result: Any) {
        try {
            val fileWriter = FileWriter(file)
            plugin.gson.toJson(result, fileWriter)
            fileWriter.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}