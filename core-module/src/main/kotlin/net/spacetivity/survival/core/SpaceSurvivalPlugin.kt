package net.spacetivity.survival.core

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.spacetivity.survival.core.chunk.ChunkManager
import net.spacetivity.survival.core.commandsystem.BukkitCommandExecutor
import net.spacetivity.survival.core.commandsystem.CommandManager
import net.spacetivity.survival.core.commandsystem.container.CommandProperties
import net.spacetivity.survival.core.commandsystem.container.ICommandExecutor
import net.spacetivity.survival.core.database.DatabaseFile
import net.spacetivity.survival.core.listener.TestListener
import net.spacetivity.survival.core.region.RegionManager
import net.spacetivity.survival.core.translation.TranslatableText
import net.spacetivity.survival.core.translation.TranslationManager
import net.spacetivity.survival.core.translation.Translator
import net.spacetivity.survival.core.translation.serialization.TranslatableTextTypeAdapter
import net.spacetivity.survival.core.utils.FileUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class SpaceSurvivalPlugin: JavaPlugin() {

    lateinit var gson: Gson
    lateinit var fileUtils: FileUtils
    lateinit var translationManager: TranslationManager
    lateinit var translator: Translator
    lateinit var commandManager: CommandManager
    lateinit var chunkManager: ChunkManager
    lateinit var regionManager: RegionManager

    override fun onEnable() {
        this.gson = GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(TranslatableText::class.java, TranslatableTextTypeAdapter(this))
            .create()

        this.fileUtils = FileUtils(this)
        this.translationManager = TranslationManager(this)
        this.translator = Translator(this)
        this.commandManager = CommandManager()
        this.chunkManager = ChunkManager(this)
        this.regionManager = RegionManager(this)

        val dbProperties: DatabaseFile = createOrLoadDatabaseProperties()

        Database.connect(
            "jdbc:mariadb://${dbProperties.hostname}:${dbProperties.port}/${dbProperties.database}",
            driver = "org.mariadb.jdbc.Driver",
            user = dbProperties.user,
            password = dbProperties.password,
        )

        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(ChunkManager.ChunkStorage, RegionManager.RegionStorage)
        }

        println(translationManager.cachedTranslations.size)

        translationManager.cachedTranslations.forEach { translatableText ->
            println(translatableText.key)
        }

        server.pluginManager.registerEvents(TestListener(this), this)
        server.pluginManager.registerEvents(TestListener(this), this)

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, Runnable {
            Bukkit.getOnlinePlayers().forEach { player: Player? ->
                val chunkOwner = chunkManager.getChunkOwner(player!!.chunk)
                val ownerDisplayName = if (chunkOwner == null) "unclaimed" else Bukkit.getOfflinePlayer(chunkOwner).name
                val color: TextColor = if (chunkOwner == null) NamedTextColor.GREEN else NamedTextColor.RED
                player.sendActionBar(Component.text(if (chunkOwner == null && regionManager.cachedClaimedRegions[player.uniqueId] == null) "Chunk is available for purchase."
                    else if (chunkOwner == null && regionManager.cachedClaimedRegions[player.uniqueId] != null) "This is unclaimed land... Maybe some other player will settle down here!"
                    else "Chunk is claimed by $ownerDisplayName.").color(color))
            }
        }, 0, 20)
    }

    private fun createOrLoadDatabaseProperties(): DatabaseFile {
        val databaseFilePath = File("${dataFolder.toPath()}/database")
        val result: DatabaseFile

        if (!Files.exists(databaseFilePath.toPath())) {
            Files.createDirectories(databaseFilePath.toPath())
        }

        val file: File = Paths.get("${databaseFilePath}/mysql.json").toFile()

        if (!Files.exists(file.toPath())) {
            result = DatabaseFile("37.114.42.32", 3306, "space_survival", "root", "-")
            fileUtils.saveFile(file, result)
        } else {
            result = fileUtils.readFile(file, DatabaseFile::class.java)!!
        }

        return result
    }

    fun registerCommand(commandExecutor: ICommandExecutor) {
        BukkitCommandExecutor::class.java.getDeclaredConstructor(CommandProperties::class.java, this::class.java)
            .newInstance(commandManager.registerCommand(commandExecutor), this)
    }

    fun isNumeric(toCheck: String): Boolean {
        return toCheck.all { char -> char.isDigit() }
    }
}