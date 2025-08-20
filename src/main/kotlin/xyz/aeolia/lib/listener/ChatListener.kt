package xyz.aeolia.lib.listener

import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import xyz.aeolia.lib.manager.StatusManager
import xyz.aeolia.lib.plaintext
import xyz.aeolia.lib.sender.MessageSender

object ChatListener : Listener {
  @EventHandler(priority = EventPriority.HIGH)
  fun onChat(event: AsyncChatEvent) {
    val plaintext = event.message().plaintext()
    if (matcher(plaintext)) {
      MessageSender.sendMessage(event.player, "To use commands, type /<command>.", true)
      event.isCancelled = true
      return
    }
    if (StatusManager.getStatus("lockchat") && !event.player.hasPermission("lib.lockchat.exempt")) {
      event.isCancelled = true
      MessageSender.sendMessage(event.player, "Chat has been locked by a moderator.", true)
    }
  }

  fun matcher(text: String): Boolean {
    if (text[0] != ':') return false
    var previousChar: Char? = null
    val text = text.toMutableList()
    text.removeAt(0)
    if (text.isEmpty()) return false
    text.forEach { c -> previousChar?.let { if (it == c) return false }; previousChar = c }
    return true
  }
}