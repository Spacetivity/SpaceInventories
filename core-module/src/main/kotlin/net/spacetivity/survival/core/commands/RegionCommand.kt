package net.spacetivity.survival.core.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.spacetivity.survival.core.SpaceSurvivalPlugin
import net.spacetivity.survival.core.commandsystem.container.CommandProperties
import net.spacetivity.survival.core.commandsystem.container.ICommandExecutor
import net.spacetivity.survival.core.commandsystem.container.ICommandSender
import net.spacetivity.survival.core.translation.TranslationKey
import net.spacetivity.survival.core.translation.Translator
import org.bukkit.entity.Player

@CommandProperties(name = "region", "survival.region", aliases = ["rg"])
class RegionCommand : ICommandExecutor {

    private val regionManager = SpaceSurvivalPlugin.instance.regionManager

    override fun execute(sender: ICommandSender, args: List<String>) {
        if (!sender.isPlayer) return
        val player: Player = sender.castTo(Player::class.java)

        if (args.size == 1) {
            when (args[0]) {
                "unclaim" -> {
                    val claimedRegion = regionManager.getRegion(player.uniqueId)

                    if (claimedRegion == null) {
                        Translator.sendMessage(player, TranslationKey.PLAYER_NO_REGION_FOUND)
                        return
                    }

                    regionManager.unclaimRegion(player.uniqueId)
                    Translator.sendMessage(player, TranslationKey.PLAYER_REGION_UNCLAIMED)

                }
                "expand" -> {

                    val region = regionManager.getRegion(player.uniqueId)

                    if (region == null) {
                        player.sendMessage(Component.text("You don't have a region!", NamedTextColor.RED))
                        return
                    }

                    SpaceSurvivalPlugin.instance.regionExpansionManager.initExpandProcess(player, region)

                }
                "info" -> {

                    val region = regionManager.getRegion(player.uniqueId)

                    if (region == null) {
                        player.sendMessage(Component.text("You don't own a region yet!", NamedTextColor.RED))
                        return
                    }

                    player.sendMessage(Component.text("Statistics about your region:", NamedTextColor.YELLOW))
                    player.sendMessage(Component.text("- Chunks claimed: ${region.chunksClaimed}", NamedTextColor.GRAY))
                    player.sendMessage(Component.text("- Owner: ${region.getOwner().name}", NamedTextColor.GRAY))
                    player.sendMessage(Component.text("- Members: ${region.trustedPlayers.size} (/region members)", NamedTextColor.GRAY))
                    
                }
                else -> sendUsage(sender)
            }
        }
    }

    override fun sendUsage(sender: ICommandSender) {
        //TODO:
        /*
            /region unclaim
            /region expand
            /region info | Lists all claimed chunks
         */
    }

    override fun onTabComplete(sender: ICommandSender, args: List<String>): MutableList<String> {
        return mutableListOf("unclaim", "expand", "info")
    }

}