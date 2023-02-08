package net.spacetivity.survival.core.land

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.title.Title
import net.spacetivity.survival.core.SpaceSurvivalPlugin
import net.spacetivity.survival.core.location.MCLoc
import net.spacetivity.survival.core.translation.TranslationKey
import net.spacetivity.survival.core.translation.Translator
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class LandManager(val plugin: SpaceSurvivalPlugin) {

    val cachedClaimedLands: MutableMap<UUID, Land> = mutableMapOf()
    val table = LandStorage

    fun isInLand(player: Player): Boolean {
        return plugin.chunkManager.cachedClaimedChunks.containsValue(Pair(player.chunk.x, player.chunk.z))
    }

    fun isLandOwner(player: Player): Boolean {
        val chunkOwner = plugin.chunkManager.getChunkOwner(player.chunk)
        return chunkOwner == player.uniqueId
    }

    fun initLand(player: Player) {
        if (cachedClaimedLands[player.uniqueId] != null) {
            player.showTitle(Title.title(
                Component.text("Warning!").color(NamedTextColor.DARK_RED),
                Component.text("You already own a region...").color(NamedTextColor.RED)
            ))
            return
        }

        val result = plugin.chunkManager.claimChunk(player.uniqueId, player.chunk, false)
        val color: TextColor = if (result.isSuccess) NamedTextColor.GREEN else NamedTextColor.RED

        player.sendActionBar(Component.text("Claim status ${result.name} | Owner is: ${
            plugin.chunkManager.getChunkOwner(player.chunk)
                ?.let { Bukkit.getOfflinePlayer(it).name }
        }").color(color))

        if (!result.isSuccess) return

        val newLand = Land(player.uniqueId, 1, true, mutableListOf(), mutableListOf())
        registerLand(newLand)

        player.sendMessage(Translator.getTranslation(TranslationKey.REGION_CREATED))

        transaction {
            LandStorage.insert {
                it[ownerId] = newLand.ownerId.toString()
                it[chunksClaimed] = newLand.chunksClaimed
                it[open] = newLand.open
                it[trustedPlayers] = plugin.gson.toJson(newLand.trustedPlayers)
                it[locations] = plugin.gson.toJson(newLand.locations)
            }
        }
    }

    fun unclaimLand(ownerId: UUID) {
        //TODO: unprotect chests
        //TODO: remove all settings and set chunk settings to standard

        // first delete all claimed chunks from player
        plugin.chunkManager.unclaimAllChunksFromPlayer(ownerId)
        
        // then delete the region object from the player
        unregisterLand(ownerId)
        transaction {
            LandStorage.deleteWhere { LandStorage.ownerId eq ownerId.toString() }
        }
    }

    fun loadLand(ownerId: UUID) {
        transaction {
            LandStorage.select { LandStorage.ownerId eq ownerId.toString() }.limit(1).firstOrNull()?.let { row ->
                val region = Land(
                    UUID.fromString(row[table.ownerId]),
                    row[table.chunksClaimed],
                    row[table.open],
                    plugin.gson.fromJson(row[table.trustedPlayers], Array<UUID>::class.java).toMutableList(),
                    plugin.gson.fromJson(row[table.locations], Array<MCLoc>::class.java).toMutableList()
                )
                cachedClaimedLands.put(ownerId, region)
            }
        }
    }

    fun registerLand(newRegion: Land) = cachedClaimedLands.putIfAbsent(newRegion.ownerId, newRegion)
    fun unregisterLand(ownerId: UUID) = cachedClaimedLands.remove(ownerId)
    fun getLand(ownerId: UUID): Land? = cachedClaimedLands[ownerId]

    object LandStorage : Table("claimed_lands") {
        val ownerId: Column<String> = varchar("ownerId", 50)
        val chunksClaimed: Column<Int> = integer("chunksClaimed")
        val open: Column<Boolean> = bool("open")
        val trustedPlayers: Column<String> = text("trustedPlayers")
        val locations: Column<String> = text("locations")
    }
}