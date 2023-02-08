package net.spacetivity.survival.core.commandsystem

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.spacetivity.survival.core.SpaceSurvivalPlugin
import net.spacetivity.survival.core.commandsystem.container.CommandProperties
import net.spacetivity.survival.core.commandsystem.container.ICommandExecutor
import net.spacetivity.survival.core.commandsystem.container.ICommandSender
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class BukkitCommandExecutor(command: CommandProperties, plugin: SpaceSurvivalPlugin) : CommandExecutor, TabCompleter {
    private val commandExecutor: ICommandExecutor
    private val command: CommandProperties

    init {
        commandExecutor = plugin.commandManager.getCommandExecutor(command.name)!!
        this.command = command

        val pluginCommand: Command = object : Command(command.name) {
            override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
                return onCommand(sender, this, commandLabel, args)
            }

            override fun tabComplete(
                sender: CommandSender,
                alias: String,
                args: Array<String>
            ): MutableList<String> {
                return onTabComplete(sender, this, alias, args)
            }
        }

        pluginCommand.aliases = command.aliases.toMutableList()
        pluginCommand.permission = command.permission
        plugin.server.commandMap.register("", pluginCommand)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val commandSender: ICommandSender = BukkitCommandSender(sender)
        if (commandSender.isPlayer && this.command.permission.isNotBlank() && !sender.hasPermission(this.command.permission)) {
            commandSender.castTo(Player::class.java)
                .sendMessage(Component.text("You are not permitted to execute that command!").color(NamedTextColor.RED))
            return true
        }

        commandExecutor.execute(commandSender, listOf(*args))
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): MutableList<String> {
        val commandSender: ICommandSender = BukkitCommandSender(sender)
        return commandExecutor.onTabComplete(commandSender, args.toMutableList())
    }
}