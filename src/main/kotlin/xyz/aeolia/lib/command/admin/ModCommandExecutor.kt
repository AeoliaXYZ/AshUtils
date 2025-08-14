package xyz.aeolia.lib.command.admin

import com.earth2me.essentials.Essentials
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import xyz.aeolia.lib.manager.UserManager.getUser
import xyz.aeolia.lib.player
import xyz.aeolia.lib.sender.MessageSender.sendMessage
import xyz.aeolia.lib.user
import xyz.aeolia.lib.utils.Message.Error.MISSING_DEPEND
import xyz.aeolia.lib.utils.Message.Generic.COMMAND_USAGE

class ModCommandExecutor : CommandExecutor {
  override fun onCommand(senderIn: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
    val sender = senderIn.player() ?: return true
    val user = sender.user()
    val toSet: Boolean
    if (args.isEmpty()) {
      toSet = !user.modMode
    } else when (args[0]) {
      "true" -> toSet = true
      "false" -> toSet = false
      "status" -> {
        sendMessage(
          sender, "Mod mode is currently " + (if (user.modMode)
            "enabled."
          else
            "disabled."), true
        )
        return true
      }

      else -> {
        sendMessage(sender, COMMAND_USAGE, true)
        return false
      }
    }
    user.modMode = toSet
    sendMessage(sender, "Mod mode " + (if (toSet) "enabled." else "disabled."), true)

    (Bukkit.getPluginManager().getPlugin("essentials") as? Essentials ?: run {
      sendMessage(sender, MISSING_DEPEND.format("Essentials"), true)
      return true
    }).getUser(sender).isSocialSpyEnabled = toSet

    return true
  }
}