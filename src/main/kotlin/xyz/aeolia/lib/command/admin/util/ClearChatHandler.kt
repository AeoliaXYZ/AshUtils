package xyz.aeolia.lib.command.admin.util

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import xyz.aeolia.lib.sender.MessageSender

class ClearChatHandler {
  companion object {
    @JvmStatic
    fun doClearChat() : Boolean {
      Bukkit.getOnlinePlayers().forEach { player ->
        if(!player.hasPermission("libls.clearchat.exempt")) {
          repeat(100) {
            player.sendMessage(Component.text(" "))
          }
          return@forEach
        }
        MessageSender.sendMessage(player, "Chat cleared for non-exempt users.")
      }
      return true
    }
  }
}