package net.spacetivity.survival.core.region.entity

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.spacetivity.survival.core.SpaceSurvivalPlugin
import net.spacetivity.survival.core.chunk.ChunkManager
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Shulker
import org.bukkit.metadata.FixedMetadataValue

class RegionSelector(val owner: Player, val chunk: Chunk, var isSelected: Boolean = false) {

    private val chunkManager: ChunkManager = SpaceSurvivalPlugin.instance.chunkManager
    private val regionSelectorManager: RegionSelectorManager = SpaceSurvivalPlugin.instance.regionSelectorManager

    lateinit var entity: Shulker
    var status: Status

    init {
        status = if (chunkManager.isChunkClaimed(chunk) && chunkManager.getChunkOwner(chunk) != owner.uniqueId) {
            Status.CLAIMED
        } else if (chunkManager.isChunkClaimed(chunk) && chunkManager.getChunkOwner(chunk) == owner.uniqueId) {
            Status.CLAIMED_BY_YOURSELF
        } else {
            Status.AVAILABLE
        }

        regionSelectorManager.registerSelector(this)
    }

    fun spawn(yLevel: Double) {
        val center: Location = chunkManager.getChunkCenterLocation(yLevel, chunk)
        this.entity = owner.world.spawnEntity(center, EntityType.SHULKER) as Shulker

        entity.setAI(false)
        entity.setGravity(false)

        entity.isCustomNameVisible = true
        entity.setMetadata(
            "chunkExpansionProcess_${owner.uniqueId}",
            FixedMetadataValue(SpaceSurvivalPlugin.instance, Pair(chunk.x, chunk.z))
        )

        setCustomName()
    }

    fun update(isActive: Boolean) {
        if (isActive) {
            status = Status.SELECTED_FOR_CLAIMING
            isSelected = true

            highlight(true)
            showChunkOutline(entity.location.blockY)
        } else {
            if (status != Status.AVAILABLE) {
                status = Status.AVAILABLE
                isSelected = false
                highlight(false)
                showChunkOutline(entity.location.blockY)
            }
        }
    }

    private fun showChunkOutline(yLevel: Int) {
        val minX: Int = chunk.x * 16
        val minZ: Int = chunk.z * 16
        val material = if (isSelected) Material.GOLD_BLOCK else Material.AIR

        for (x in minX..minX + 16) {
            for (y in yLevel until yLevel + 1) {
                for (z in minZ..minZ + 16) {

                    val locs: List<Location> = listOf(
                        Location(owner.world, minX.toDouble(), y.toDouble(), z.toDouble()),
                        Location(owner.world, x.toDouble(), y.toDouble(), minZ.toDouble()),
                        Location(owner.world, minX + 16.0, y.toDouble(), z.toDouble()),
                        Location(owner.world, x.toDouble(), y.toDouble(), minZ + 16.0)
                    )

                    locs.forEach { loc ->
                        loc.block.setType(material, true)
                    }
                }
            }
        }
    }

    private fun highlight(isActive: Boolean) {
        entity.isGlowing = isActive
        setCustomName()
    }

    private fun setCustomName() {
        entity.customName(
            when (status) {
                Status.CLAIMED -> {
                    val ownerName: String = Bukkit.getOfflinePlayer(chunkManager.getChunkOwner(chunk)!!).name!!
                    Component.text("This chunk is claimed by $ownerName.", NamedTextColor.RED, TextDecoration.BOLD)
                }

                Status.CLAIMED_BY_YOURSELF -> Component.text(
                    "You own this chunk!",
                    NamedTextColor.RED,
                    TextDecoration.BOLD
                )

                Status.SELECTED_FOR_CLAIMING -> Component.text("Selected", NamedTextColor.YELLOW, TextDecoration.BOLD)
                Status.AVAILABLE -> Component.text(
                    "Chunk: ${chunk.x}, ${chunk.z}",
                    NamedTextColor.GREEN,
                    TextDecoration.BOLD
                )
            }
        )
    }

    enum class Status(val availableForSelection: Boolean) {
        CLAIMED(false),
        CLAIMED_BY_YOURSELF(false),
        SELECTED_FOR_CLAIMING(false),
        AVAILABLE(true),
    }
}