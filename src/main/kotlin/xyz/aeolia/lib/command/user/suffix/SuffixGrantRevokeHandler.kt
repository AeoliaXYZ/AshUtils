package xyz.aeolia.lib.command.user.suffix

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import xyz.aeolia.lib.data.SubCommandHandler
import xyz.aeolia.lib.manager.PermissionManager
import xyz.aeolia.lib.manager.UserMapManager
import xyz.aeolia.lib.menu.SuffixMenu
import xyz.aeolia.lib.sender.MessageSender
import xyz.aeolia.lib.utils.Message

class SuffixGrantRevokeHandler(private val plugin: JavaPlugin, val status: Boolean) : SubCommandHandler() {
  override val alias = listOf(if (status) "grant" else "revoke")

  override fun handle(sender: CommandSender, args: Array<out String>): Boolean {
    if (!sender.hasPermission("lib.suffix-grant")) {
      if (sender is Player) SuffixMenu(plugin).inventory.open(sender)
      return true
    }

    val suffixList = plugin.config.getStringList("suffix.list")

    if (args.size != 2) return invalidExecution(sender)
    val uuid = UserMapManager.getUuidFromName(args[0])
    if (uuid == null) {
      MessageSender.sendMessage(sender, Message.Player.NOT_FOUND)
      return true
    }

    if (!suffixList.contains(args[1])) {
      MessageSender.sendMessage(sender, "Invalid suffix!")
      return true
    }
    if (PermissionManager.permissionUpdate(uuid, "lib.suffix." + args[1], status)) {
      if (!status) {
        PermissionManager.groupUpdate(plugin, uuid, args[1], false)
      }
      MessageSender.sendMessage(
        sender, ("Successfully " + (if (status) "granted " else "revoked ")
                + args[1] + " for " + args[0] + ".")
      )
      return true
    }
    MessageSender.sendMessage(
      sender, "Something went wrong trying to modify permissions. " +
              "Please check the console."
    )
    return true
  }
  companion object {
    fun invalidExecution(sender: CommandSender): Boolean {
      MessageSender.sendMessage(sender, Message.Generic.COMMAND_USAGE)
      MessageSender.sendMessage(sender, "/suffix [grant/revoke] <user> <suffix>", false)
      return true
    }
  }
}