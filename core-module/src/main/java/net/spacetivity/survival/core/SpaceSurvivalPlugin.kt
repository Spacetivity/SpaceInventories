package net.spacetivity.survival.core

import net.spacetivity.survival.core.commandsystem.BukkitCommandExecutor
import net.spacetivity.survival.core.commandsystem.CommandManager
import net.spacetivity.survival.core.commandsystem.container.CommandProperties
import net.spacetivity.survival.core.commandsystem.container.ICommandExecutor
import net.spacetivity.survival.core.commands.GamemodeCommand
import net.spacetivity.survival.core.message.MessageRepository
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

class SpaceSurvivalPlugin : JavaPlugin() {

    lateinit var commandManager: CommandManager
    lateinit var messageRepository: MessageRepository

    override fun onEnable() {
        this.commandManager = CommandManager()
        this.messageRepository = MessageRepository(this)

        Database.connect(
            "jbc:mariadb://185.117.0.240:3306",
            driver = "org.mariadb.jdbc.Driver",
            user = "root",
            password = "e2WYrq6TkjaHkHj5wt6m"
        )

        transaction {
            addLogger(StdOutSqlLogger)
        }

        registerCommand(GamemodeCommand(this))
    }

    fun registerCommand(commandExecutor: ICommandExecutor) {
        val properties = commandManager.registerCommand(commandExecutor)
        BukkitCommandExecutor::class.java.getDeclaredConstructor(CommandProperties::class.java)
            .newInstance(properties, this)
    }

    fun isNumeric(toCheck: String): Boolean {
        return toCheck.all { char -> char.isDigit() }
    }
}