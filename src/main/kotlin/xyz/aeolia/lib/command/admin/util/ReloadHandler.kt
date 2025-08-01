package xyz.aeolia.lib.command.admin.util

import org.bukkit.command.CommandSender
import xyz.aeolia.lib.AeoliaLib
import xyz.aeolia.lib.manager.EnchantmentManager
import xyz.aeolia.lib.manager.KitManager
import xyz.aeolia.lib.sender.MessageSender.sendMessage

class ReloadHandler(private val lib: AeoliaLib) : SubCommandHandler() {
  override val alias = listOf("reload")

  override fun handle(sender: CommandSender, args: Array<out String>): Boolean {
    sendMessage(sender, "Starting config reload...", true)
    val response = lib.configManager.loadConfig()
    var compose = "Reload complete! "
    if (response[0] == -1) {
      compose += "Your config file was empty, deleted, or unreadable. It has been replaced with the default."
    } else if (response[0]!! > 0 || response[1]!! > 0) {
      compose += ("Added " + response[0] + " value" + (if (response[0] == 1) "" else "s") + ", removed "
              + response[1] + " value" + (if (response[1] == 1) "" else "s") + ".")
    }
    sendMessage(sender, compose, true)
    sendMessage(sender, "Starting kits reload...", true)
    KitManager.init(lib, sender)
    sendMessage(sender, "Starting enchants reload...", true)
    EnchantmentManager.reloadEnchantments(sender)
    return true
  }
}