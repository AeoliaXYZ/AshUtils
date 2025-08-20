package xyz.aeolia.lib.manager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import net.kyori.adventure.audience.Audience
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import xyz.aeolia.lib.data.Kit
import xyz.aeolia.lib.sender.MessageSender
import xyz.aeolia.lib.utils.Message.Error.GENERIC
import java.io.File

object KitManager {
  val kits = mutableMapOf<String, Kit>()
  private lateinit var plugin: JavaPlugin
  private var loaded = false
  private val json = Json { prettyPrint = true }

  fun init(plugin: JavaPlugin, recipient: Audience? = null) {
    this.plugin = plugin
    loaded = false
    CoroutineScope(Dispatchers.Default).launch {
      initCoroutine(recipient)
      loaded = true
    }
  }

  @Deprecated("cleanup is no longer necessary, please remove the call to it in your code")
  fun cleanup() {
    plugin.logger.warning("A plugin has called the deprecated method KitManager.cleanup(), " +
            "this will become an error in 2.2.0.")
  }

  private suspend fun initCoroutine(recipient: Audience?) = withContext(Dispatchers.IO) {
    kits.clear()
    val folder = File(plugin.dataFolder, "kits").apply { mkdirs() }
    folder.listFiles()?.forEach {
      if (!it.isFile) return@forEach
      try {
        val id = it.nameWithoutExtension
        val kit = json.decodeFromString<Kit>(it.readText()).apply { this.id = id }
        kits[id] = kit
      } catch (e: Exception) {
        plugin.logger.warning("Exception while reading ${it.name}: ${e.message}")
      }
    }
    recipient?.let { MessageSender.sendMessage(it, "Kit reload complete! ${kits.size} kits loaded.") }
  }

  fun givePlayerKit(player: Player, kit: Kit): Boolean {
    if (!loaded) return false
    val items = kit.items.toMutableList().apply {
      kits["__global__"]?.let { addAll(it.items) }
    }
    player.inventory.clear()
    items.forEach {
      val stack = it.loadStack() ?: run {
        MessageSender.sendMessage(player, GENERIC)
        plugin.logger.warning("Error processing item $it in kit ${kit.id}")
        return false
      }
      if (!equipIfArmor(player, stack)) {
        player.inventory.addItem(stack)
      }
    }
    return true
  }

  fun equipIfArmor(player: Player, stack: ItemStack): Boolean {
    val id = stack.type.name
    when {
      "HELMET" in id -> player.inventory.helmet = stack
      "CHESTPLATE" in id -> player.inventory.chestplate = stack
      "LEGGINGS" in id -> player.inventory.leggings = stack
      "BOOTS" in id -> player.inventory.boots = stack
      "SHIELD" in id -> player.inventory.setItemInOffHand(stack)
      else -> return false
    }
    return true
  }
}
