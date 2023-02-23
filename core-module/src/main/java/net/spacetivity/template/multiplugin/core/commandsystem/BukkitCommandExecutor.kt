package net.spacetivity.template.singleplugin.commandsystem

import net.spacetivity.template.multiplugin.core.CoreModuleBootstrap
import net.spacetivity.template.singleplugin.commandsystem.container.CommandProperties
import net.spacetivity.template.singleplugin.commandsystem.container.ICommandExecutor
import net.spacetivity.template.singleplugin.commandsystem.container.ICommandSender
import net.spacetivity.template.singleplugin.translation.TranslationKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class BukkitCommandExecutor(command: CommandProperties, plugin: CoreModuleBootstrap) : CommandExecutor, TabCompleter {
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
            val player: Player = sender as Player
            CoreModuleBootstrap.instance.translator.sendMessage(player, TranslationKey.COMMAND_NO_PERMISSION)
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