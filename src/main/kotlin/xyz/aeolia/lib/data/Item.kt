package xyz.aeolia.lib.data

import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import xyz.aeolia.lib.manager.EnchantmentManager
import xyz.aeolia.lib.sender.MessageSender

@Serializable
class Item(
  val displayName: String? = null,
  val lore: List<String>? = null,
  val material: String,
  val amount: Int = 1,
  val enchantments: MutableMap<String, Int>? = null
) {

  val materialUpper = material.uppercase()

  val woolTypes: List<Material> by lazy {
    listOf(
      Material.WHITE_WOOL,
      Material.ORANGE_WOOL,
      Material.RED_WOOL,
      Material.GRAY_WOOL,
      Material.YELLOW_WOOL,
      Material.BLUE_WOOL,
      Material.PURPLE_WOOL,
      Material.PINK_WOOL,
      Material.BROWN_WOOL,
      Material.CYAN_WOOL,
      Material.LIGHT_BLUE_WOOL,
      Material.LIME_WOOL
    )
  }

  fun loadStack(): ItemStack? {
    val mm = MessageSender.miniMessage

    val material = try {
      Material.getMaterial(materialUpper)
        ?: throw Throwable("Material ${this.materialUpper} not found")
    } catch (_: Throwable) {
      if (materialUpper == "WOOL") {
        woolTypes.random()
      } else {
        MessageSender.sendMessage(Bukkit.getConsoleSender(), "Material ${this.material.uppercase()} not found")
        return null
      }
    }

    val stack = ItemStack.of(material, this.amount)
    val meta = stack.itemMeta!!

    this.displayName?.let {
      meta.displayName(mm.deserialize(it))
    }

    this.lore?.let {
      val lore = mutableListOf<Component>()
      it.forEach { line ->
        lore.add(mm.deserialize(line))
      }
      meta.lore(lore)
    }

    this.enchantments?.forEach {
      val enchantment = EnchantmentManager.nameToEnchant(it.key) ?: return null
      meta.addEnchant(enchantment, it.value, true)
    }

    stack.itemMeta = meta
    return stack
  }
}