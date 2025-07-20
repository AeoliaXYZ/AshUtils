package xyz.aeolia.lib.command.admin.util

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import xyz.aeolia.lib.sender.MessageSender

class ClearChatHandler {
  companion object {
    @JvmStatic
    fun doClearChat() : Boolean {
      for (player in Bukkit.getOnlinePlayers()) {
        if(!player.hasPermission("libls.clearchat.exempt")) {
          repeat (100) {
            player.sendMessage(Component.text(" "))
          }
        } else {
          MessageSender.sendMessage(player, "Chat cleared for non-exempt users.")
        }
      }
      return true
    }
  }
}