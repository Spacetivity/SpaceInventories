package net.spacetivity.survival.core.region

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.title.Title
import net.spacetivity.survival.core.SpaceSurvivalPlugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class RegionManager(val plugin: SpaceSurvivalPlugin) {

    val cachedClaimedRegions: MutableMap<UUID, ClaimedRegion> = mutableMapOf()
    val table = RegionStorage

    fun isInRegion(player: Player): Boolean {
        return plugin.chunkManager.cachedClaimedChunks.containsValue(Pair(player.chunk.x, player.chunk.z))
    }

    fun isRegionOwner(player: Player): Boolean {
        val chunkOwner = plugin.chunkManager.getChunkOwner(player.chunk)
        return chunkOwner == player.uniqueId
    }

    fun initRegion(player: Player) {
        if (cachedClaimedRegions[player.uniqueId] != null) {
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
                ?.let { Bukkit.getOfflinePlayer(it) }
        }").color(color))

        if (!result.isSuccess) return

        val newRegion = ClaimedRegion(player.uniqueId, 1, true, mutableListOf())
        registerRegion(newRegion)

        transaction {
            RegionStorage.insert {
                it[ownerId] = newRegion.ownerId.toString()
                it[chunksClaimed] = newRegion.chunksClaimed
                it[open] = newRegion.open
                it[trustedPlayers] = plugin.gson.toJson(newRegion.trustedPlayers)
            }
        }
    }

    fun loadRegion(ownerId: UUID) {
        transaction {
            RegionStorage.select { RegionStorage.ownerId eq ownerId.toString() }.limit(1).firstOrNull()?.let { row ->
                val region = ClaimedRegion(
                    UUID.fromString(row[table.ownerId]),
                    row[table.chunksClaimed],
                    row[table.open],
                    plugin.gson.fromJson(row[table.trustedPlayers], Array<UUID>::class.java).toMutableList()
                )
                cachedClaimedRegions.put(ownerId, region)
            }
        }
    }

    fun registerRegion(newRegion: ClaimedRegion) = cachedClaimedRegions.putIfAbsent(newRegion.ownerId, newRegion)
    fun unregisterRegion(ownerId: UUID) = cachedClaimedRegions.remove(ownerId)
    fun getRegion(ownerId: UUID): ClaimedRegion? = cachedClaimedRegions[ownerId]

    object RegionStorage : Table("claimed_regions") {
        val ownerId: Column<String> = varchar("ownerId", 50)
        val chunksClaimed: Column<Int> = integer("chunksClaimed")
        val open: Column<Boolean> = bool("open")
        val trustedPlayers: Column<String> = text("trustedPlayers")
    }
}