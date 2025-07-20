package xyz.aeolia.lib.menu

import cymru.asheiou.inv.ClickableItem
import cymru.asheiou.inv.SmartInventory
import cymru.asheiou.inv.content.InventoryContents
import cymru.asheiou.inv.content.InventoryProvider
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.plugin.java.JavaPlugin
import xyz.aeolia.lib.listener.PVPListener
import xyz.aeolia.lib.manager.KitManager
import xyz.aeolia.lib.manager.UserManager
import xyz.aeolia.lib.sender.MessageSender
import java.io.File
import kotlin.math.ceil

open class PVPMenu(open val plugin: JavaPlugin) : InventoryProvider {
  val inventory = SmartInventory.builder()
    .id("pvpMenu")
    .provider(this)
    .size(getRows(), 9)
    .type(InventoryType.CHEST)
    .title("Kits")
    .build()!!

  override fun init(player: Player, contents: InventoryContents) {
    KitManager.kits.forEach { kit ->
      if(kit.value.id == "__global__") return@forEach
      val condition = try {
        player.hasPermission(kit.value.permission)
      } catch (_: NullPointerException) {
        true
      }
      val displayItem = kit.value.displayItem.loadStack()?: run {
        MessageSender.sendMessage(player, "displayItem in kit ${kit.key} failed to deserialize!")
        return@forEach
      }
      if (condition) contents.add(ClickableItem.of(displayItem) { e ->
        e.isCancelled = true
        PVPListener.clearBlocks(UserManager.getUser(player))
        KitManager.givePlayerKit(player, kit.value)
        MessageSender.sendMessage(player, "Equipped kit ${kit.value.displayName}!")
        PVPListener.tpPlayerToArena(player, plugin)
      })
    }
  }

  fun getRows(): Int {
    val folder = File(plugin.dataFolder, "kits")
    var fileCount = folder.listFiles().size
    folder.listFiles().forEach { file ->
      if (file.isFile) if (file.nameWithoutExtension == "__global__") fileCount -= 1
    }
    return ceil(fileCount / 9.toDouble()).toInt()
  }
}