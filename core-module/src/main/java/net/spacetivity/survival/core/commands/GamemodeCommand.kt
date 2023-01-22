package net.spacetivity.survival.core.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.spacetivity.survival.core.SpaceSurvivalPlugin
import net.spacetivity.survival.core.commandsystem.container.CommandProperties
import net.spacetivity.survival.core.commandsystem.container.ICommandExecutor
import net.spacetivity.survival.core.commandsystem.container.ICommandSender
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player

@CommandProperties(name = "gamemode", "survival.gamemode", ["gm"])
class GamemodeCommand(private val plugin: SpaceSurvivalPlugin) : ICommandExecutor {

    override fun execute(sender: ICommandSender, args: List<String>) {
        if (!sender.isPlayer) return
        val player = sender as Player

        if (args.isNotEmpty() && !plugin.isNumeric(args[0])) {
            player.sendMessage(
                Component.text("Please define a number to choose the gamemode!").color(NamedTextColor.RED)
            )
            return
        }


    }

    override fun sendUsage(sender: ICommandSender) {
        TODO("Not yet implemented")
    }

    override fun onTabComplete(sender: ICommandSender, args: List<String>): MutableList<String> {
        if (args.size == 1) return mutableListOf("0", "1", "2", "3")
        if (args.size == 2) return Bukkit.getOnlinePlayers().map { player -> player.name }.toMutableList()
        return mutableListOf()
    }

    fun getGameModeFromInt(gameModeId: Int) = when (gameModeId) {
        0 -> GameMode.SURVIVAL
        1 -> GameMode.CREATIVE
        2 -> GameMode.ADVENTURE
        3 -> GameMode.SPECTATOR
        else -> GameMode.ADVENTURE
    }
}