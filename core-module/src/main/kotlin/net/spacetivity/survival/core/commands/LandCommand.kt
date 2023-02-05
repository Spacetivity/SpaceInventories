package net.spacetivity.survival.core.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.spacetivity.survival.core.SpaceSurvivalPlugin
import net.spacetivity.survival.core.chunk.ClaimResult
import net.spacetivity.survival.core.commandsystem.container.CommandProperties
import net.spacetivity.survival.core.commandsystem.container.ICommandExecutor
import net.spacetivity.survival.core.commandsystem.container.ICommandSender
import net.spacetivity.survival.core.translation.TranslationKey
import net.spacetivity.survival.core.translation.Translator
import net.spacetivity.survival.core.utils.LandUtils
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.entity.Player

@CommandProperties(name = "land", "survival.land")
class LandCommand : ICommandExecutor {

    private val chunkManager = SpaceSurvivalPlugin.instance.chunkManager
    private val landManager = SpaceSurvivalPlugin.instance.landManager

    override fun execute(sender: ICommandSender, args: List<String>) {
        if (!sender.isPlayer) return
        val player: Player = sender.castTo(Player::class.java)

        if (args.size == 1) {
            when (args[0]) {
                "show" -> {

                    Bukkit.getScheduler().runTaskTimer(SpaceSurvivalPlugin.instance, Runnable {
                        LandUtils.showClaimedChunks(player, player.location.blockY + 10)
                    }, 0L, 20L)

                }

                "addChunk" -> {

                    if (landManager.isInLand(player)) {
                        player.sendMessage(Component.text("You are already in your region!", NamedTextColor.RED))
                        return
                    }

                    val chunk: Chunk = player.chunk

                    if (chunkManager.isChunkClaimed(chunk)) {
                        player.sendMessage(Component.text("This chunk is already claimed!", NamedTextColor.RED))
                        return
                    }

                    val result: ClaimResult = chunkManager.claimChunk(player.uniqueId, chunk, true)

                    player.sendMessage(
                        Component.text(
                            if (result.isSuccess) "Successfully claimed chunk (${chunk.x} | ${chunk.z})" else "Error: ${result.name}",
                            if (result.isSuccess) NamedTextColor.GREEN else NamedTextColor.RED
                        )
                    )

                }

                "unclaim" -> {

                    val claimedRegion = landManager.getLand(player.uniqueId)

                    if (claimedRegion == null) {
                        Translator.sendMessage(player, TranslationKey.PLAYER_NO_REGION_FOUND)
                        return
                    }

                    landManager.unclaimLand(player.uniqueId)
                    Translator.sendMessage(player, TranslationKey.PLAYER_REGION_UNCLAIMED)

                }

                "info" -> {

                    val region = landManager.getLand(player.uniqueId)

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

    }

    override fun onTabComplete(sender: ICommandSender, args: List<String>): MutableList<String> {
        return mutableListOf("unclaim", "addChunk", "info")
    }

}