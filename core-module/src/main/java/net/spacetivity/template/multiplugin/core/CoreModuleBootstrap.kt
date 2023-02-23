package net.spacetivity.template.multiplugin.core

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.spacetivity.template.singleplugin.api.StorageType
import net.spacetivity.template.singleplugin.commandsystem.BukkitCommandExecutor
import net.spacetivity.template.singleplugin.commandsystem.CommandManager
import net.spacetivity.template.singleplugin.commandsystem.container.CommandProperties
import net.spacetivity.template.singleplugin.commandsystem.container.ICommandExecutor
import net.spacetivity.template.singleplugin.configs.ConfigFile
import net.spacetivity.template.singleplugin.configs.DatabaseFile
import net.spacetivity.template.singleplugin.translation.TranslatableText
import net.spacetivity.template.singleplugin.translation.TranslationRepository
import net.spacetivity.template.singleplugin.translation.Translator
import net.spacetivity.template.singleplugin.translation.serialization.TranslatableTextTypeAdapter
import net.spacetivity.template.singleplugin.utils.FileUtils
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class CoreModuleBootstrap : JavaPlugin() {

    lateinit var gson: Gson
    lateinit var fileUtils: FileUtils
    lateinit var translationRepository: TranslationRepository
    lateinit var configFile: ConfigFile
    lateinit var storageType: StorageType
    lateinit var translator: Translator
    lateinit var commandManager: CommandManager

    init {
        instance = this
    }

    override fun onEnable() {
        this.gson = GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(TranslatableText::class.java, TranslatableTextTypeAdapter())
            .create()

        this.fileUtils = FileUtils()
        this.translationRepository = TranslationRepository(mutableListOf(), this.dataFolder.toPath())
        this.configFile = createOrLoadGlobalProperties()
        this.storageType = StorageType.valueOf(this.configFile.storageType)
        this.translator = Translator(this.translationRepository.cachedTranslations)
        this.commandManager = CommandManager()

        when (storageType) {
            StorageType.MARIADB -> {
                initializeMySQL()
            }

            StorageType.JSON -> {

            }
        }
    }

    private fun initializeMySQL() {
        val dbProperties: DatabaseFile = createOrLoadDatabaseProperties()

        Database.connect(
            "jdbc:mariadb://${dbProperties.hostname}:${dbProperties.port}/${dbProperties.database}",
            driver = "org.mariadb.jdbc.Driver",
            user = dbProperties.user,
            password = dbProperties.password,
        )

        transaction {
            addLogger(StdOutSqlLogger)
            // SchemaUtils.create(TableName)
        }
    }

    private fun createOrLoadGlobalProperties(): ConfigFile {
        val configFilePath = File("${dataFolder}/config")
        val result: ConfigFile

        if (!Files.exists(configFilePath.toPath())) Files.createDirectories(configFilePath.toPath())

        val file: File = Paths.get("${configFilePath}/config.json").toFile()

        if (!Files.exists(file.toPath())) {
            result = ConfigFile(StorageType.JSON.name)
            fileUtils.saveFile(file, result)
        } else {
            result = fileUtils.readFile(file, ConfigFile::class.java)!!
        }

        return result
    }

    private fun createOrLoadDatabaseProperties(): DatabaseFile {
        val databaseFilePath = File("${dataFolder}/database")
        val result: DatabaseFile

        if (!Files.exists(databaseFilePath.toPath())) Files.createDirectories(databaseFilePath.toPath())

        val file: File = Paths.get("${databaseFilePath}/mysql.json").toFile()

        if (!Files.exists(file.toPath())) {
            result = DatabaseFile("37.114.42.32", 3306, "space_survival", "root", "-")
            fileUtils.saveFile(file, result)
        } else {
            result = fileUtils.readFile(file, DatabaseFile::class.java)!!
        }

        return result
    }

    private fun registerCommand(commandExecutor: ICommandExecutor) {
        BukkitCommandExecutor::class.java.getDeclaredConstructor(CommandProperties::class.java, this::class.java)
            .newInstance(commandManager.registerCommand(commandExecutor), this)
    }

    companion object {

        @JvmStatic
        lateinit var instance: CoreModuleBootstrap
            private set

    }
}