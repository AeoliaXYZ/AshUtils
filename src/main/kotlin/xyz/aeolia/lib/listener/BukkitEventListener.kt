package xyz.aeolia.lib.listener

import com.earth2me.essentials.Essentials
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
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
import xyz.aeolia.lib.sender.MessageSender
import xyz.aeolia.lib.task.MessageLaterTask
import xyz.aeolia.lib.task.ROEQuitTask
import xyz.aeolia.lib.task.UserPruneTask
import xyz.aeolia.lib.user
import java.util.regex.Pattern

class BukkitEventListener(private val plugin: JavaPlugin) : Listener {
  private val pattern: Pattern = Pattern.compile("^:[A-Z]{4,}$")
  private val ess: Essentials = Bukkit.getPluginManager().getPlugin("Essentials") as Essentials

  @EventHandler(priority = EventPriority.LOWEST)
  fun onPlayerQuit(event: PlayerQuitEvent) {
    event.player.user().online = false
    ROEQuitTask(this.plugin).runTaskLater(this.plugin, 3)
    UserPruneTask(event.player, plugin).runTaskLater(this.plugin, plugin.config.getLong("prune-time"))
  }

  @EventHandler(priority = EventPriority.HIGH)
  fun onChat(event: AsyncChatEvent) {
    val plainText = PlainTextComponentSerializer.plainText().serialize(event.message())
    if (pattern.matcher(plainText).matches()) {
      MessageSender.sendMessage(event.player, "To use commands, type /<command>.", true)
      event.isCancelled = true
      return
    }
    if (StatusManager.getStatus("lockchat") && !event.player.hasPermission("lib.lockchat.exempt")) {
      event.isCancelled = true
      MessageSender.sendMessage(event.player, "Chat has been locked by a moderator.", true)
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  fun onPlayerJoin(event: PlayerJoinEvent) {
    val player = event.player
    val refUUID = UserMapManager.getUuidFromName(player.name)
    if (refUUID != null) {
      if (refUUID == player.uniqueId) {
        plugin.logger.info(player.name + " is already registered to users.json.")
      } else {
        plugin.logger.info(player.name + " is registered to users.json as another UUID! Correcting.")
      }
    } else {
      plugin.logger.info(player.name + " is not registered to users.json. Adding them.")
    }
    UserMapManager.putUserInMap(player.name, player.uniqueId)

    val user = player.user()
    user.online = true
    user.vanish = ess.getUser(player).isVanished

    if (MotdHandler.motd != null) {
      // Send MOTD if it exists
      MessageLaterTask(player, MotdHandler.motd!!).runTaskLater(plugin, 20L)
    }
  }
}