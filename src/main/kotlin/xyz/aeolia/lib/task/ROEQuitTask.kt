package xyz.aeolia.lib.task

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import xyz.aeolia.lib.manager.StatusManager

class ROEQuitTask(val plugin: JavaPlugin) : BukkitRunnable() {
  override fun run() {
    plugin.logger.info("Player left, checking if the server should restart...")
    val roeEnabled = StatusManager.getStatus("restartonempty")

    if (roeEnabled && Bukkit.getOnlinePlayers().isEmpty()) {
      plugin.logger.info("Restarting...")
      Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart")
      return
    }

    plugin.logger.info("No restart because " + (if (roeEnabled) "server is not empty." else "ROE is not enabled."))
  }
}