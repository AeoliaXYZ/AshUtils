package xyz.aeolia.lib

import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.aeolia.lib.data.User
import xyz.aeolia.lib.manager.UserManager
import xyz.aeolia.lib.sender.MessageSender
import xyz.aeolia.lib.utils.Message.Generic.NOT_PLAYER

fun CommandSender.player(): Player? = if (this is Player) this else {
  MessageSender.sendMessage(this, NOT_PLAYER, true)
  null
}

fun OfflinePlayer.user(retain: Boolean = true): User = UserManager.getUser(this, retain)