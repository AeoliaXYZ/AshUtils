package xyz.aeolia.lib.command.admin.util

import org.bukkit.command.CommandSender
import xyz.aeolia.lib.sender.MessageSender
import xyz.aeolia.lib.sender.WebhookSender
import xyz.aeolia.lib.utils.Message.Error.REQUEST_FAIL_GENERIC

object MotdHandler : SubCommandHandler() {
  @JvmStatic
  var motd: String? = null
  override val alias = listOf("motd")

  override fun handle(sender: CommandSender, args: Array<out String>): Boolean {
    if (args.isEmpty()) {
      motd = null
      MessageSender.sendMessage(sender, "MOTD reset!")
      return true
    }
    motd = args.joinToString(" ")
    MessageSender.sendMessage(sender, "MOTD set to:")
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