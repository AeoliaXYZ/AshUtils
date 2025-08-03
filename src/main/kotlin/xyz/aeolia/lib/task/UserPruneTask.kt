package xyz.aeolia.lib.task

import org.bukkit.OfflinePlayer
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import xyz.aeolia.lib.manager.UserManager

class UserPruneTask(val player: OfflinePlayer, val plugin: JavaPlugin) : BukkitRunnable() {
  override fun run() {
    if (UserManager.getUser(player, true).online) return
    UserManager.removeUser(player)
    plugin.logger.info("[AeoliaLib] Pruned user ${player.name}")
  }
}