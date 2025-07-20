package xyz.aeolia.lib.serializable

import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import xyz.aeolia.lib.sender.MessageSender

@Serializable
class Item(
  val displayName: String? = null,
  val lore: List<String>? = null,
  val material: String,
  val amount: Int = 1,
  val enchantments: MutableMap<String, Int>? = null
) {
  fun loadStack(): ItemStack? {
    val mm = MessageSender.Companion.miniMessage
    val material = Material.getMaterial(this.material.uppercase())?: run {
      MessageSender.Companion.sendMessage(Bukkit.getConsoleSender(), "Material ${this.material.uppercase()} not found")
      return null
    }
    val stack = ItemStack.of(material, this.amount)
    val meta = stack.itemMeta
    if (this.displayName != null) {
      meta.displayName(mm.deserialize(displayName))
    }

    if (this.lore != null) {
      val lore = mutableListOf<Component>()
      this.lore.forEach { line ->
        lore.add(mm.deserialize(line))
      }
      meta.lore(lore)
    }

    this.enchantments?.forEach{ enchant ->
      val registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
      val enchantment = registry.get(NamespacedKey.minecraft(enchant.key)) ?: run {
        MessageSender.Companion.sendMessage(Bukkit.getConsoleSender(), "Invalid enchantment key: ${enchant.key}")
        return null
      }
      meta.addEnchant(enchantment, enchant.value, true)
    }
    stack.itemMeta = meta
    MessageSender.Companion.sendMessage(Bukkit.getConsoleSender(), "Loaded stack $stack")
    return stack
  }
}