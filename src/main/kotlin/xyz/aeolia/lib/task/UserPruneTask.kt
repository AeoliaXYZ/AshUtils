package xyz.aeolia.lib.task

import org.bukkit.OfflinePlayer
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import xyz.aeolia.lib.manager.UserManager
import xyz.aeolia.lib.user

class UserPruneTask(val player: OfflinePlayer, val plugin: JavaPlugin) : BukkitRunnable() {
  override fun run() {
    if (player.user(false).online) return
    UserManager.removeUser(player)
    plugin.logger.info("[AeoliaLib] Pruned user ${player.name}")
  }
}