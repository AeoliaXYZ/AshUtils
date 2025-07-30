package xyz.aeolia.lib.command.admin

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.StringUtil
import xyz.aeolia.lib.sender.MessageSender
import xyz.aeolia.lib.utils.Message.Generic.COMMAND_USAGE
import xyz.aeolia.lib.utils.Message.Generic.NOT_PLAYER_ARGS
import java.util.*

class FakeTabExecutor(private val plugin: JavaPlugin) : TabExecutor {
  override fun onCommand(
    sender: CommandSender,
    command: Command,
    label: String,
    args: Array<out String>) : Boolean {
    if (sender !is Player && args.size == 1) {
      MessageSender.sendMessage(sender, NOT_PLAYER_ARGS, true)
      return true
    }
    if (args.isEmpty()) {
      MessageSender.sendMessage(sender, COMMAND_USAGE, true)
      return false
    }
    var message = when (args[0]) {
      "quit", "leave", "q" -> plugin.config.getString("quit-message")
      "join", "j" -> plugin.config.getString("join-message")
      else -> {
        MessageSender.sendMessage(sender, COMMAND_USAGE, true)
        return false
      }
    }
    checkNotNull(message)

    message = when (args.size) {
      1 -> {
        message.replace("{USERNAME}", sender.name)
      }
      2 -> {
        message.replace("{USERNAME}", args[1])
      }
      else -> {
        MessageSender.sendMessage(sender, COMMAND_USAGE, true)
        return false
      }
    }
    val deserialized = MessageSender.miniMessage.deserialize(message)
    Bukkit.getServer().broadcast(deserialized)
    return true
  }

  override fun onTabComplete(
    sender: CommandSender,
    cmd: Command,
    label: String,
    args: Array<String>
  ): MutableList<String> {
    val completions: MutableList<String> = ArrayList<String>()
    val commands: MutableList<String> = ArrayList<String>()

    if (args.size == 1) {
      if (sender.hasPermission("lib.fake")) {
        commands.add("join")
        commands.add("quit")
      }
    }
    StringUtil.copyPartialMatches<MutableList<String>>(args[0], commands, completions)
    completions.sort()
    return completions
  }
}