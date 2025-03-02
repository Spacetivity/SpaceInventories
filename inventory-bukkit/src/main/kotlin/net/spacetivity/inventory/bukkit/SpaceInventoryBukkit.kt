package net.spacetivity.inventory.bukkit

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.spacetivity.inventory.api.SpaceInventoryProvider
import net.spacetivity.inventory.bukkit.api.InventoryApiImpl
import net.spacetivity.inventory.bukkit.file.MessageFile
import net.spacetivity.inventory.bukkit.file.SoundConfigFile
import net.spacetivity.inventory.bukkit.listener.InventoryPlayerListener
import net.spacetivity.inventory.bukkit.utils.FileUtils
import org.bukkit.plugin.java.JavaPlugin

class SpaceInventoryBukkit : JavaPlugin() {

    lateinit var soundConfigFile: SoundConfigFile
    lateinit var messageFile: MessageFile

    override fun onEnable() {
        instance = this

        this.soundConfigFile = createOrLoadSoundConfigFile()
        this.messageFile = createOrLoadMessageFile()

        val inventoryApi = InventoryApiImpl()
        SpaceInventoryProvider.register(inventoryApi)

        InventoryPlayerListener(this)
    }

    private fun createOrLoadSoundConfigFile(): SoundConfigFile {
        return FileUtils.createOrLoadFile(dataFolder.toPath(), "global", "sounds", SoundConfigFile::class, SoundConfigFile())
    }

    private fun createOrLoadMessageFile(): MessageFile {
        return FileUtils.createOrLoadFile(dataFolder.toPath(), "global", "messages", MessageFile::class, MessageFile())
    }

    companion object {
        val GSON: Gson = GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create()

        @JvmStatic
        lateinit var instance: SpaceInventoryBukkit
            private set
    }

}
