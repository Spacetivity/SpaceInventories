package net.spacetivity.survival.core

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.spacetivity.survival.core.chunk.ChunkManager
import net.spacetivity.survival.core.chunk.ChunkPlayer
import net.spacetivity.survival.core.chunk.PlayerChunkManager
import net.spacetivity.survival.core.commandsystem.BukkitCommandExecutor
import net.spacetivity.survival.core.commandsystem.CommandManager
import net.spacetivity.survival.core.commandsystem.container.CommandProperties
import net.spacetivity.survival.core.commandsystem.container.ICommandExecutor
import net.spacetivity.survival.core.listener.TestListener
import net.spacetivity.survival.core.message.MessageRepository
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

class SpaceSurvivalPlugin : JavaPlugin() {

    val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    lateinit var commandManager: CommandManager
    lateinit var messageRepository: MessageRepository
    lateinit var chunkManager: ChunkManager
    lateinit var playerChunkManager: PlayerChunkManager

    override fun onEnable() {
        this.commandManager = CommandManager()
        this.messageRepository = MessageRepository(this)
        this.chunkManager = ChunkManager()
        this.playerChunkManager = PlayerChunkManager()

        Database.connect(
            "jdbc:mariadb://37.114.42.32:3306/space_survival",
            driver = "org.mariadb.jdbc.Driver",
            user = "root",
            password = "e2WYrq6TkjaHkHj5wt6m",
        )

        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(ChunkPlayer.ChunkStorage)
        }

        server.pluginManager.registerEvents(TestListener(this), this)
    }

    fun registerCommand(commandExecutor: ICommandExecutor) {
        val properties = commandManager.registerCommand(commandExecutor)
        BukkitCommandExecutor::class.java.getDeclaredConstructor(CommandProperties::class.java, this::class.java).newInstance(properties, this)
    }

    fun isNumeric(toCheck: String): Boolean {
        return toCheck.all { char -> char.isDigit() }
    }
}