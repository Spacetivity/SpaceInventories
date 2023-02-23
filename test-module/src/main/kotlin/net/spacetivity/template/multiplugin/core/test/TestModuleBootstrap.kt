package net.spacetivity.template.multiplugin.core.test

import net.spacetivity.template.multiplugin.core.CoreModuleBootstrap
import net.spacetivity.template.singleplugin.api.StorageType
import org.bukkit.plugin.java.JavaPlugin

class TestModuleBootstrap : JavaPlugin() {

    override fun onEnable() {
        // Example for accessing the core module
        val configFile = CoreModuleBootstrap.instance.configFile
        val storageType = configFile.storageType
        println("DB Type: $storageType | Available: ${StorageType.values().joinToString(", ") { it.name }}")
    }

    override fun onDisable() {

    }

}