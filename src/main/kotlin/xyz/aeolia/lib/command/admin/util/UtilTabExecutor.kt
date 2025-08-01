package xyz.aeolia.lib.command.admin.util

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.util.StringUtil
import xyz.aeolia.lib.AeoliaLib
import xyz.aeolia.lib.sender.MessageSender.sendMessage
import xyz.aeolia.lib.utils.Message.Generic.COMMAND_USAGE

class UtilTabExecutor(val lib: AeoliaLib) : TabExecutor {
  val subCommands: MutableList<SubCommandHandler> = mutableListOf(
    ReloadHandler(lib),
    ClearChatHandler,
    MotdHandler
  )

  @Suppress("DEPRECATION")
  override fun onCommand(
    sender: CommandSender,
    cmd: Command,
    label: String,
    args: Array<out String>
  ): Boolean {
    if (args.isEmpty()) {
      sendMessage(sender, "AeoliaLib v" + lib.description.version + " enabled.", true)
      return true
    }
    val subCommands: MutableList<SubCommandHandler> = ArrayList(this.subCommands)
    subCommands.add(StatusToggleHandler(args[0]))
    val subCommandArgs = args.copyOfRange(1, args.size)

    for (subCommand in subCommands) {
      if (subCommand.alias.contains(args[0])) {
        return subCommand.handle(sender, subCommandArgs)
      }
    }

    sendMessage(sender, COMMAND_USAGE, true)
    sendMessage(sender, "/util reload/restartonempty/motd/clearchat/lockchat.", false)
    return true
  }

  override fun onTabComplete(
    sender: CommandSender,
    cmd: Command,
    label: String,
    args: Array<String>
  ): MutableList<String> {
    val completions: MutableList<String> = mutableListOf()
    val commands: MutableList<String> = mutableListOf()

    if (args.size == 1) {
      if (sender.hasPermission("lib.admin")) {
        commands.add("motd")
        commands.add("reload")
        commands.add("restartonempty")
        commands.add("lockchat")
        commands.add("clearchat")
      }
      StringUtil.copyPartialMatches<MutableList<String>>(args[0], commands, completions)
    } else if (args.size == 2) {
      if (args[0] == "restartonempty" || args[0] == "lockchat") {
        if (sender.hasPermission("lib.admin")) {
          commands.add("true")
          commands.add("false")
          commands.add("status")
        }
      }
      StringUtil.copyPartialMatches<MutableList<String>>(args[1], commands, completions)
    }
    completions.sort()
    return completions
  }
}