package xyz.aeolia.lib.task

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import xyz.aeolia.lib.manager.UserManager

class UserPruneTask(val player: Player) : BukkitRunnable() {
  override fun run() {
    if (UserManager.getUser(player).online) return
    UserManager.removeUser(player)
    Bukkit.getLogger().info("[AeoliaLib] Pruned user ${player.name}")
  }
}