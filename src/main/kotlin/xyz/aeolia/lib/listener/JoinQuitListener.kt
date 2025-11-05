package xyz.aeolia.lib.listener

import com.earth2me.essentials.Essentials
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import xyz.aeolia.lib.command.admin.util.MotdHandler
import xyz.aeolia.lib.manager.StatusManager
import xyz.aeolia.lib.manager.UserMapManager
import xyz.aeolia.lib.task.MessageLaterTask
import xyz.aeolia.lib.task.ROEQuitTask
import xyz.aeolia.lib.task.UserPruneTask
import xyz.aeolia.lib.task.VanishJoinTask
import xyz.aeolia.lib.user
import xyz.aeolia.lib.utils.Message

class JoinQuitListener(private val plugin: JavaPlugin) : Listener {
  private val ess: Essentials? = Bukkit.getPluginManager().getPlugin("Essentials") as? Essentials

  @EventHandler(priority = EventPriority.LOW)
  fun onPlayerJoin(event: PlayerJoinEvent) {
    val player = event.player
    val refUUID = UserMapManager.getUuidFromName(player.name)

    plugin.logger.info(
      when (refUUID) {
        null -> "${player.name} is not registered to users.json. Adding them."
        player.uniqueId -> "${player.name} is already registered to users.json."
        else -> "${player.name} is registered to users.json as another UUID! Correcting."
      }
    )

    UserMapManager.putUserInMap(player.name, player.uniqueId)

    player.user().online = true

    VanishJoinTask(ess!!.getUser(player)).runTaskLater(plugin, 1L)
    MotdHandler.motd?.let { MessageLaterTask(player, it).runTaskLater(plugin, 5L) }

    if(StatusManager.getStatus("lockchat"))
      MessageLaterTask(player, Message.Chat.LOCKED).runTaskLater(plugin, 20L)
  }


  @EventHandler(priority = EventPriority.LOWEST)
  fun onPlayerQuit(event: PlayerQuitEvent) {
    event.player.user().online = false
    ROEQuitTask(plugin).runTaskLater(plugin, 3)
    UserPruneTask(event.player, plugin).runTaskLater(plugin, plugin.config.getLong("prune-time"))
  }
}