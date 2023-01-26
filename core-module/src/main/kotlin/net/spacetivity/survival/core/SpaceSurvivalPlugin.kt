package net.spacetivity.survival.core

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.spacetivity.survival.core.chunk.ChunkManager
import net.spacetivity.survival.core.chunk.PlayerChunkManager
import net.spacetivity.survival.core.chunk.container.ChunkPlayer
import net.spacetivity.survival.core.commandsystem.BukkitCommandExecutor
import net.spacetivity.survival.core.commandsystem.CommandManager
import net.spacetivity.survival.core.commandsystem.container.CommandProperties
import net.spacetivity.survival.core.commandsystem.container.ICommandExecutor
import net.spacetivity.survival.core.listener.TestListener
import net.spacetivity.survival.core.message.MessageRepository
import org.bukkit.Bukkit
import org.bukkit.entity.Player
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
            password = "dJ5s4L^0^!8O",
        )

        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(ChunkPlayer.ChunkStorage)
        }

        server.pluginManager.registerEvents(TestListener(this), this)

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, Runnable {
            Bukkit.getOnlinePlayers().forEach { player: Player? ->
                val chunkOwner = chunkManager.getChunkOwner(player!!.chunk)
                val ownerDisplayName = if (chunkOwner == null) "unclaimed" else Bukkit.getOfflinePlayer(chunkOwner).name
                val color: TextColor = if (chunkOwner == null) NamedTextColor.GREEN else NamedTextColor.RED
                player.sendActionBar(Component.text(if (chunkOwner == null) "Chunk is available for purchase." else "Chunk is claimed by $ownerDisplayName.").color(color))
            }
        }, 0, 20)
    }

    fun registerCommand(commandExecutor: ICommandExecutor) {
        BukkitCommandExecutor::class.java.getDeclaredConstructor(CommandProperties::class.java, this::class.java)
            .newInstance(commandManager.registerCommand(commandExecutor), this)
    }

    fun isNumeric(toCheck: String): Boolean {
        return toCheck.all { char -> char.isDigit() }
    }
}