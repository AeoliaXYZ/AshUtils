package xyz.aeolia.lib.command.admin

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import xyz.aeolia.lib.manager.UserMapManager
import xyz.aeolia.lib.miniMessage
import xyz.aeolia.lib.player
import xyz.aeolia.lib.sender.MessageSender
import xyz.aeolia.lib.user
import xyz.aeolia.lib.utils.Message.Error.CONFIG
import xyz.aeolia.lib.utils.Message.Generic.COMMAND_USAGE
import xyz.aeolia.lib.utils.Message.Player.NOT_FOUND
import xyz.aeolia.lib.utils.Message.Player.OFFLINE

class MiniMessageCommandExecutor(val plugin: JavaPlugin) : CommandExecutor {
  override fun onCommand(
    senderIn: CommandSender,
    command: Command,
    label: String,
    argsInput: Array<out String>
  ): Boolean {
    val sender = senderIn.player() ?: return true
    var args = argsInput
    if (args.isEmpty()) {
      MessageSender.sendMessage(sender, COMMAND_USAGE, true)
      return false
    }

    val identity: Player
    if (args[0].startsWith('@')) {
      val sudo = args[0].substring(1)
      val uuid = UserMapManager.getUuidFromName(sudo) ?: run {
        MessageSender.sendMessage(sender, NOT_FOUND, true)
        return false
      }
      val user = Bukkit.getOfflinePlayer(uuid).user()
      if (user.online) {
        identity = Bukkit.getPlayer(uuid) ?: run {
          MessageSender.sendMessage(sender, OFFLINE, true)
          return true
        }
        args = args.drop(1).toTypedArray()
      } else {
        MessageSender.sendMessage(sender, OFFLINE, true)
        return true
      }
    } else {
      identity = sender
    }

    val message = args.joinToString(" ").miniMessage()
    var chatFormat = plugin.config.getString("chat-format")
    if (chatFormat == null) {
      MessageSender.sendMessage(sender, String.format(CONFIG, "chat-format"))
      return true
    }
    chatFormat = chatFormat.replace("{DISPLAYNAME}", identity.displayName().miniMessage())
    chatFormat += " "
    val toSend = Component.text()
      .append { chatFormat.miniMessage() }
      .append { message }
      .build()

    Bukkit.getServer().sendMessage(toSend)
    return true
  }
}