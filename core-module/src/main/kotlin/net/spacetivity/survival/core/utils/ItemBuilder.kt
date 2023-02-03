package net.spacetivity.survival.core.utils

import net.kyori.adventure.text.Component
import net.spacetivity.survival.core.SpaceSurvivalPlugin
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class ItemBuilder(material: Material) {

    var itemStack: ItemStack = ItemStack(material)
    lateinit var itemMeta: ItemMeta
    lateinit var action: (PlayerInteractEvent) -> (Unit)

    init {
        if (itemStack.type != Material.AIR) itemMeta = itemStack.itemMeta
    }

    fun setName(name: Component): ItemBuilder {
        itemMeta.displayName(name)
        itemStack.itemMeta = itemMeta
        return this
    }

    fun setName(name: String): ItemBuilder {
        itemMeta.displayName(Component.text(name))
        itemStack.itemMeta = itemMeta
        return this
    }


    fun setLoreByString(lore: MutableList<String>): ItemBuilder {
        itemMeta.lore(lore.map { s: String -> Component.text(s) })
        itemStack.itemMeta = itemMeta
        return this
    }

    fun setLoreByComponent(lore: MutableList<Component>): ItemBuilder {
        itemMeta.lore(lore)
        itemStack.itemMeta = itemMeta
        return this
    }

    fun setAmount(amount: Int): ItemBuilder {
        itemStack.amount = amount
        return this
    }

    fun addEnchantment(enchantment: Enchantment, level: Int): ItemBuilder {
        itemMeta.addEnchant(enchantment, level, true)
        itemStack.itemMeta = itemMeta
        return this
    }

    fun addFlags(vararg flag: ItemFlag): ItemBuilder {
        itemMeta.addItemFlags(*flag)
        itemStack.itemMeta = itemMeta
        return this
    }

    fun setUnbreakable(): ItemBuilder {
        itemMeta.isUnbreakable = true
        itemStack.itemMeta = itemMeta
        return this
    }

    fun onInteract(action: (PlayerInteractEvent) -> (Unit)): ItemBuilder {
        this.action = action
        SpaceSurvivalPlugin.clickableItems.add(this)
        return this
    }

    fun build(): ItemStack {
        return itemStack
    }

}