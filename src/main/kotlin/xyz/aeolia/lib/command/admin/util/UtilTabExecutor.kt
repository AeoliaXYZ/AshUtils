package xyz.aeolia.lib.command.admin.util

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.util.StringUtil
import xyz.aeolia.lib.AeoliaLib
import xyz.aeolia.lib.data.SubCommandHandler
import xyz.aeolia.lib.sender.MessageSender.sendMessage
import xyz.aeolia.lib.utils.Message.Generic.COMMAND_USAGE

class UtilTabExecutor(val lib: AeoliaLib) : TabExecutor {
  val subCommands: MutableList<SubCommandHandler> = mutableListOf(
    ReloadHandler(lib),
    ClearChatHandler,
    MotdHandler,
    DumpHandler
  )

  @Suppress("DEPRECATION")
  override fun onCommand(
    sender: CommandSender,
    cmd: Command,
    label: String,
    args: Array<out String>
  ): Boolean {
    if (args.isEmpty()) return true.also { sendMessage(sender,
        "AeoliaLib v" + lib.description.version + " enabled.", true) }

    val subCommands: MutableList<SubCommandHandler> = mutableListOf<SubCommandHandler>(StatusToggleHandler(args[0]))
      .apply { addAll(subCommands) }
    val subCommandArgs = args.copyOfRange(1, args.size)

    subCommands.forEach {
      if (it.alias.contains(args[0])) {
        return it.handle(sender, subCommandArgs)
      }
    }

    return true.also { sendMessage(sender,
      "$COMMAND_USAGE\n/util reload/restartonempty/motd/clearchat/lockchat.", true) }
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
      if (sender.hasPermission("lib.admin"))
        commands.addAll(listOf("motd", "reload", "restartonempty", "lockchat", "clearchat", "dump"))
      StringUtil.copyPartialMatches<MutableList<String>>(args[0], commands, completions)
    } else if (args.size == 2) {
      if (args[0] == "restartonempty" || args[0] == "lockchat") if (sender.hasPermission("lib.admin"))
        commands.addAll(listOf("true", "false", "status"))
      if (args[0] == "dump")
        commands.addAll(Bukkit.getOnlinePlayers().map { it.name })
      StringUtil.copyPartialMatches<MutableList<String>>(args[1], commands, completions)
    }
    completions.sort().also { return completions }
  }
}