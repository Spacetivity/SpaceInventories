package net.spacetivity.survival.core.utils

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class ItemBuilder(material: Material) {

    var itemStack: ItemStack = ItemStack(material)
    var itemMeta: ItemMeta = itemStack.itemMeta

    fun name(name: Component): ItemBuilder {
        itemMeta.displayName(name)
        itemStack.itemMeta = itemMeta
        return this
    }

    fun name(name: String): ItemBuilder {
        itemMeta.displayName(Component.text(name))
        itemStack.itemMeta = itemMeta
        return this
    }


    fun loreByString(lore: MutableList<String>): ItemBuilder {
        itemMeta.lore(lore.map { s: String -> Component.text(s) })
        itemStack.itemMeta = itemMeta
        return this
    }

    fun loreByComponent(lore: MutableList<Component>): ItemBuilder {
        itemMeta.lore(lore)
        itemStack.itemMeta = itemMeta
        return this
    }

    fun amount(amount: Int): ItemBuilder {
        itemStack.amount = amount
        return this
    }

    fun enchantment(enchantment: Enchantment, level: Int): ItemBuilder {
        itemMeta.addEnchant(enchantment, level, true)
        itemStack.itemMeta = itemMeta
        return this
    }

    fun flags(vararg flag: ItemFlag): ItemBuilder {
        itemMeta.addItemFlags(*flag)
        itemStack.itemMeta = itemMeta
        return this
    }

    fun unbreakable(): ItemBuilder {
        itemMeta.isUnbreakable = true
        itemStack.itemMeta = itemMeta
        return this
    }

    fun build(): ItemStack {
        return itemStack
    }

}