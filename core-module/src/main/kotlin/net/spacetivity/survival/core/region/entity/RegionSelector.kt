package net.spacetivity.survival.core.region.entity

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.spacetivity.survival.core.SpaceSurvivalPlugin
import net.spacetivity.survival.core.chunk.ChunkManager
import net.spacetivity.survival.core.utils.RGBUtils.toRGBCode
import org.bukkit.*
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Shulker

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
        setCustomName()
    }

    fun update(isActive: Boolean, yLevel: Int) {
        if (isActive) {
            val lastActiveSelector = regionSelectorManager.getActiveSelector(owner)
            lastActiveSelector?.isSelected = false
            //TODO: To achieve old results do: lastActiveSelector?.status = Status.AVAILABLE
            lastActiveSelector?.highlight(false)

            status = Status.SELECTED_FOR_CLAIMING
            isSelected = true

            highlight(true)
        }

        showChunkOutline(yLevel)
    }

    fun showChunkOutline(yLevel: Int) {
        val minX: Int = entity.chunk.x * 16
        val minZ: Int = entity.chunk.z * 16

        val colorCode: Triple<Int, Int, Int> = when(status) {
            Status.CLAIMED -> toRGBCode(NamedTextColor.RED)
            Status.CLAIMED_BY_YOURSELF -> toRGBCode(NamedTextColor.RED)
            Status.SELECTED_FOR_CLAIMING -> toRGBCode(NamedTextColor.YELLOW)
            Status.AVAILABLE -> toRGBCode(NamedTextColor.GREEN)
        }

        val dustOptions: Particle.DustOptions = Particle.DustOptions(
            Color.fromRGB(colorCode.first, colorCode.second, colorCode.third),
            1F
        )

        for (x in minX until minX + 17) for (y in yLevel until yLevel + 1) for (z in minZ until minZ + 17) {
            owner.spawnParticle(Particle.REDSTONE, minX.toDouble(), y.toDouble(), z.toDouble(), 20, dustOptions)
            owner.spawnParticle(Particle.REDSTONE, x.toDouble(), y.toDouble(), minZ.toDouble(), 20, dustOptions)
            owner.spawnParticle(Particle.REDSTONE, minX + 17.0, y.toDouble(), z.toDouble(), 20, dustOptions)
            owner.spawnParticle(Particle.REDSTONE, x.toDouble(), y.toDouble(), minZ + 17.0, 20, dustOptions)
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
                Status.CLAIMED_BY_YOURSELF -> Component.text("You own this chunk!", NamedTextColor.RED, TextDecoration.BOLD)
                Status.SELECTED_FOR_CLAIMING -> Component.text("Selected", NamedTextColor.YELLOW, TextDecoration.BOLD)
                Status.AVAILABLE -> Component.text("Chunk: ${chunk.x}, ${chunk.z}", NamedTextColor.GREEN, TextDecoration.BOLD)
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