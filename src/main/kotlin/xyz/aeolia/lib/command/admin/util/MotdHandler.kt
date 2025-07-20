package xyz.aeolia.lib.command.admin.util

import org.bukkit.command.CommandSender
import xyz.aeolia.lib.utils.Message.Error.REQUEST_FAIL_GENERIC
import xyz.aeolia.lib.sender.MessageSender
import xyz.aeolia.lib.sender.WebhookSender

class MotdHandler {
  companion object {
    @JvmStatic
    var motd : String? = null

    @JvmStatic
    fun handleCommand(sender: CommandSender, args: Array<out String?>?): Boolean {
      if (args?.size == 0 || args == null) {
        motd = null
        MessageSender.sendMessage(sender, "MOTD reset!")
        return true
      }
      motd = args.joinToString(" ")
      MessageSender.sendMessage(sender, "Motd set to:")
      val broadcastStatus = WebhookSender.broadcast(MessageSender.miniMessage.deserialize(motd!!))
      if (broadcastStatus) return true
      MessageSender.sendMessage(sender, REQUEST_FAIL_GENERIC)
      MessageSender.sendMessage(
        sender, "It will persist until the server restarts or you reset it by " +
                "running this command again without an argument."
      )
      return true
    }
  }
}