package xyz.aeolia.lib.command.admin.util

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import xyz.aeolia.lib.manager.UserManager
import xyz.aeolia.lib.manager.UserMapManager
import xyz.aeolia.lib.sender.MessageSender
import xyz.aeolia.lib.user
import xyz.aeolia.lib.utils.Message

object DumpHandler : SubCommandHandler() {
  override val alias: List<String> = listOf("dump")
  override fun handle(sender: CommandSender, args: Array<out String>): Boolean {
    if(args.size != 1) {
      MessageSender.sendMessage(sender, Message.Generic.COMMAND_USAGE)
      MessageSender.sendMessage(sender, "/util dump [playername]", false)
      return true
    }
    val uuid = UserMapManager.getUuidFromName(args[0]) ?: run {
      MessageSender.sendMessage(sender, Message.Player.NOT_FOUND)
      return true
    }

    val user = Bukkit.getOfflinePlayer(uuid).user()

    MessageSender.sendMessage(sender, "Data for ${args[0]}:")
    user.getData().forEach {
      MessageSender.sendMessage(sender, "${it.key}: ${it.value}", false)
    }
    return true
  }
}