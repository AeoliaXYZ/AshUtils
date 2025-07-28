package xyz.aeolia.lib.command.admin.util

import org.bukkit.command.CommandSender

abstract class SubCommandHandler {
  abstract val alias: List<String>
  abstract fun handle(sender: CommandSender, args: Array<out String>): Boolean
}