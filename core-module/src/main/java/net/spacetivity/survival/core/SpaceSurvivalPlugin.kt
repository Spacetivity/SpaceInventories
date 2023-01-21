package net.spacetivity.survival.core

import net.spacetivity.survival.core.message.MessageRepository
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

class SpaceSurvivalPlugin: JavaPlugin() {

    private lateinit var messageRepository: MessageRepository

    override fun onEnable() {
        messageRepository = MessageRepository(this)

        Database.connect("jbc:mariadb://185.117.0.240:3306", driver = "org.mariadb.jdbc.Driver", user = "root", password = "e2WYrq6TkjaHkHj5wt6m")

        transaction {
            addLogger(StdOutSqlLogger)

        }

    }

}