package xyz.aeolia.lib.command.admin

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import xyz.aeolia.lib.miniMessage
import xyz.aeolia.lib.utils.Message.Error.REQUEST_FAIL_GENERIC
import xyz.aeolia.lib.utils.Message.Generic.COMMAND_USAGE
import xyz.aeolia.lib.sender.MessageSender
import xyz.aeolia.lib.sender.WebhookSender

class BroadcastCommandExecutor(val plugin: JavaPlugin) : CommandExecutor {
  override fun onCommand(
    sender: CommandSender,
    command: Command,
    label: String,
    args: Array<out String>): Boolean {
    if (args.isEmpty()) {
      MessageSender.sendMessage(sender, COMMAND_USAGE)
    }
    val message = args.joinToString(" ").miniMessage()
    val broadcastStatus = WebhookSender.broadcast(message)
    if (broadcastStatus) return true
    MessageSender.sendMessage(sender, REQUEST_FAIL_GENERIC)
    return true
  }
}