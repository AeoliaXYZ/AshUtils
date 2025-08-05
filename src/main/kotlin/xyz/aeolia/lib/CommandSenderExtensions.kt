package xyz.aeolia.lib

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.aeolia.lib.sender.MessageSender
import xyz.aeolia.lib.utils.Message.Generic.NOT_PLAYER

fun CommandSender.player(): Player? = if (this is Player) this else {
  MessageSender.sendMessage(this, NOT_PLAYER, true)
  null
}
