package xyz.aeolia.lib.manager

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import xyz.aeolia.lib.data.EnchantResult
import xyz.aeolia.lib.sender.MessageSender
import java.io.File

object EnchantmentManager {
  lateinit var enchantments: MutableMap<Enchantment, List<Int>>
  private lateinit var plugin: JavaPlugin
  private lateinit var scope: CoroutineScope
  private var loaded = false

  val registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
  val gson = Gson()

  fun init(plugin: JavaPlugin) {
    this.plugin = plugin
    reloadEnchantments()
  }

  fun reloadEnchantments() {
    loaded = false
    scope = CoroutineScope(Dispatchers.Default)
    scope.launch {
      reloadEnchantmentsCoro()
      loaded = true
    }
  }

  private fun reloadEnchantmentsCoro() {
    val json = File(plugin.dataFolder, "enchantments.json").readText()
    val type = object : TypeToken<Map<String, List<Int>>>() {}.type
    gson.fromJson<Map<String, List<Int>>>(json, type).forEach { enchantmentString, list ->
      if (list.isEmpty()) {
        plugin.logger.severe("Missing enchantment levels for $enchantmentString!")
        return@forEach
      }
      val enchantment = nameToEnchant(enchantmentString) ?: run {
        plugin.logger.severe("Unrecognised enchantment $enchantmentString!")
        return@forEach
      }
      enchantments.put(enchantment, list)
    }
  }

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
      if (it.key.conflictsWith(enchantToAdd)) return true
    }
    return false
  }

  fun addSafeEnchant(enchantment: Enchantment, level: Int, item: ItemStack): EnchantResult {
    if (conflicts(enchantment, item)) return EnchantResult.CONFLICTING_ENCHANTMENTS
    if (enchantment.maxLevel < level) return EnchantResult.INVALID_LEVEL
    item.addEnchantment(enchantment, level)
    return EnchantResult.SUCCESS
  }

  /*
  Returns the price of the item or an error code.
   */
  fun addAndDebit(enchantment: Enchantment, level: Int, item: ItemStack, player: Player): Pair<EnchantResult, Int> {
    val econ = EconManager.getEcon()
    val enchantmentValue = enchantments.getOrElse(enchantment) {
      plugin.logger.severe("Unrecognised enchantment $enchantment!")
      return EnchantResult.INVALID_ENCHANTMENT to 0
    }
    val price = enchantmentValue.getOrElse(level - 1) {
      return EnchantResult.INVALID_LEVEL to 0
    }
    if (econ.getBalance(player) < price) return EnchantResult.INSUFFICIENT_FUNDS to 0
    val enchantResult = addSafeEnchant(enchantment, level, item)
    if (enchantResult != EnchantResult.SUCCESS)
      return enchantResult to 0
    return enchantResult to price
  }
}