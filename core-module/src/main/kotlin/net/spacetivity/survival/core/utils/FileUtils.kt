package net.spacetivity.survival.core.utils

import net.spacetivity.survival.core.SpaceSurvivalPlugin
import java.io.*

class FileUtils(val plugin: SpaceSurvivalPlugin) {

    fun <T> readFile(file: File?, cls: Class<T>?): T? {
        return try {
            plugin.gson.fromJson(file?.let { FileReader(it) }, cls)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    fun saveFile(file: File?, `object`: Any?) {
        try {
            val fileWriter = file?.let { FileWriter(it) }
            plugin.gson.toJson(`object`, fileWriter)
            fileWriter?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}