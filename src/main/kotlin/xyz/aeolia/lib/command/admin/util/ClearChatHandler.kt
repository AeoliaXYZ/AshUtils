package xyz.aeolia.lib.command.admin.util

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import xyz.aeolia.lib.sender.MessageSender

object ClearChatHandler : SubCommandHandler() {
  override val alias = listOf("clearchat")

  override fun handle(sender: CommandSender, args: Array<out String>): Boolean {
    Bukkit.getOnlinePlayers().forEach { player ->
      if (!player.hasPermission("lib.clearchat.exempt")) {
        repeat(100) { player.sendMessage(Component.text(" ")) }
        return@forEach
      }
      MessageSender.sendMessage(player, "Chat cleared for non-exempt users.")
    }
    return true
  }
}