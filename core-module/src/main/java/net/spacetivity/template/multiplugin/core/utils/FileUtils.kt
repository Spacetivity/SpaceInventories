package net.spacetivity.template.singleplugin.utils

import net.spacetivity.template.multiplugin.core.CoreModuleBootstrap
import java.io.*

class FileUtils() {

    fun <T> readFile(file: File, clazz: Class<T>): T? {
        return try {
            CoreModuleBootstrap.instance.gson.fromJson(FileReader(file), clazz)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    fun saveFile(file: File, result: Any) {
        try {
            val fileWriter = FileWriter(file)
            CoreModuleBootstrap.instance.gson.toJson(result, fileWriter)
            fileWriter.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}