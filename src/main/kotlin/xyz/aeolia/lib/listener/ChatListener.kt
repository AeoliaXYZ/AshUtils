package xyz.aeolia.lib.listener

import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import xyz.aeolia.lib.manager.StatusManager
import xyz.aeolia.lib.plaintext
import xyz.aeolia.lib.sender.MessageSender
import xyz.aeolia.lib.utils.Message

object ChatListener : Listener {
  @EventHandler(priority = EventPriority.HIGH)
  fun onChat(event: AsyncChatEvent) {
    if (StatusManager.getStatus("lockchat") && !event.player.hasPermission("lib.lockchat.exempt")) {
      event.isCancelled = true
      MessageSender.sendMessage(event.player, Message.Chat.LOCKED, true)
      return
    }
    if (matcher(event.message().plaintext())) {
      MessageSender.sendMessage(event.player, "To use commands, type /<command>.", true)
      event.isCancelled = true
    }
  }

  fun matcher(text: String): Boolean {
    if (!text.startsWith(":")) return false
    val pairs = text.drop(1).zipWithNext()
    if (pairs.isEmpty()) return false
    return pairs.all { (a, b) -> a != b }
  }
}