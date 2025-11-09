package xyz.aeolia.lib.command.user.suffix

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.StringUtil
import xyz.aeolia.lib.data.SubCommandHandler
import xyz.aeolia.lib.menu.SuffixMenu
import xyz.aeolia.lib.sender.MessageSender
import xyz.aeolia.lib.utils.Message

class SuffixCommandExecutor(private val plugin: JavaPlugin) : TabExecutor {
  val subCommands: MutableList<SubCommandHandler> = mutableListOf(
    SuffixCreateHandler(plugin),
    SuffixGrantRevokeHandler(plugin, true),
    SuffixGrantRevokeHandler(plugin, false),
  )

  override fun onCommand(
    sender: CommandSender, command: Command,
    label: String, args: Array<String>
  ): Boolean {
    if (args.isEmpty()) {
      if (sender is Player) {
        SuffixMenu(plugin).inventory.open(sender)
        return true
      } else {
        MessageSender.sendMessage(sender, "This command can only be executed by a player without an argument.")
        return true
      }
    }
    val subCommandArgs = args.copyOfRange(1, args.size)

    subCommands.firstOrNull {
      it.alias.contains(args[0])
    }?.let {
      return it.handle(sender, subCommandArgs)
    }

    MessageSender.sendMessage(sender, Message.Generic.COMMAND_USAGE)
    return false
  }


  override fun onTabComplete(
    sender: CommandSender, command: Command,
    label: String, args: Array<String>
  ): MutableList<String> {
    val completions: MutableList<String> = ArrayList()
    val commands: MutableList<String> = ArrayList()

    if (!sender.hasPermission("lib.suffix-grant")) return mutableListOf()

    if (args.size == 1) {
      commands.add("grant")
      commands.add("revoke")
      if (sender.hasPermission("lib.suffix-create")) commands.add("create")
      StringUtil.copyPartialMatches<MutableList<String>>(args[0], commands, completions)
    } else if (args.size == 2) {
      if (args[0] == "grant" || args[0] == "revoke") {
        for (p in Bukkit.getOnlinePlayers()) commands.add(p.name)
      }
      StringUtil.copyPartialMatches<MutableList<String>>(args[1], commands, completions)
    } else if (args.size == 3) {
      if (args[0] == "grant" || args[0] == "revoke") {
        commands.addAll(plugin.config.getStringList("suffix.list"))
        StringUtil.copyPartialMatches<MutableList<String>>(args[2], commands, completions)
      }
    }
    completions.sort()
    return completions
  }
}