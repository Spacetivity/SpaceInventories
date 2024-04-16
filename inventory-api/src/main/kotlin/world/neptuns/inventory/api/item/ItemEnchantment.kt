package world.neptuns.inventory.api.item

import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

data class ItemEnchantment(val enchantment: Enchantment, val strength: Int, val isActive: Boolean) {

    fun performAction(item: ItemStack) {
        if (this.isActive) item.addEnchantment(this.enchantment, this.strength)
        else item.removeEnchantment(this.enchantment)
    }

    companion object {
        fun of(enchantment: Enchantment, strength: Int, isActive: Boolean): ItemEnchantment {
            return ItemEnchantment(enchantment, strength, isActive)
        }
    }

}
