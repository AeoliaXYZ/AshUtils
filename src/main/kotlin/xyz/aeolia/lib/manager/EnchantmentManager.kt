package xyz.aeolia.lib.manager

import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import xyz.aeolia.lib.sender.MessageSender

object EnchantmentManager {
  val registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)

  fun nameToEnchant(input: String): Enchantment? {
    val name = input.lowercase()
    return registry.get(NamespacedKey.minecraft(name)) ?: run {
      MessageSender.sendMessage(
        Bukkit.getConsoleSender(),
        "Invalid enchantment key: $name"
      )
      return null
    }
  }

  fun conflicts(enchantToAdd: Enchantment, item: ItemStack): Boolean {
    item.enchantments.forEach {
      if(it.key.conflictsWith(enchantToAdd)) return true
    }
    return false
  }

  fun addSafeEnchant(enchantment: Enchantment, level: Int, item: ItemStack): ItemStack? {
    if(conflicts(enchantment, item)) return null
    if(enchantment.maxLevel < level) return null
    item.addEnchantment(enchantment, level)
    return item
  }
}