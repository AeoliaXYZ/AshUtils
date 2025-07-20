package xyz.aeolia.lib.listener;

import com.earth2me.essentials.Essentials;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.aeolia.lib.command.admin.util.MotdHandler;
import xyz.aeolia.lib.manager.StatusManager;
import xyz.aeolia.lib.manager.UserManager;
import xyz.aeolia.lib.manager.UserMapManager;
import xyz.aeolia.lib.sender.MessageSender;
import xyz.aeolia.lib.serializable.User;
import xyz.aeolia.lib.task.MessageLaterTask;
import xyz.aeolia.lib.task.ROEQuitTask;
import xyz.aeolia.lib.task.UserPruneTask;

import java.util.UUID;
import java.util.regex.Pattern;

public class BukkitEventListener implements Listener {

  private final JavaPlugin plugin;
  private final Pattern pattern = Pattern.compile("^:[A-Z]{4,}$");
  private Essentials ess;

  public BukkitEventListener(JavaPlugin plugin) {
    this.plugin = plugin;
    ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerQuit(PlayerQuitEvent event) {
    UserManager.getUser(event.getPlayer()).setOnline(false);
    new ROEQuitTask(this.plugin).runTaskLater(this.plugin, 3);
    new UserPruneTask(event.getPlayer()).runTaskLater(this.plugin, plugin.getConfig().getLong("prune-time"));

  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onChat(AsyncChatEvent event) {
    String plainText = PlainTextComponentSerializer.plainText().serialize(event.message());
    if (pattern.matcher(plainText).matches()) {
      MessageSender.sendMessage(event.getPlayer(), "To use commands, type /<command>.", true);
      event.setCancelled(true);
      return;
    }
    if (StatusManager.getStatus("lockchat") && !event.getPlayer().hasPermission("lib.lockchat.exempt")) {
      event.setCancelled(true);
      MessageSender.sendMessage(event.getPlayer(), "Chat has been locked by a moderator.", true);
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    UUID refUUID = UserMapManager.getUuidFromName(player.getName());
    if (refUUID != null) {
      if (refUUID.equals(player.getUniqueId())) {
        plugin.getLogger().info(player.getName() + " is already registered to users.json.");
      }
      else {
        plugin.getLogger().info(player.getName() + " is registered to users.json as another UUID! Correcting.");
      }
    } else {
      plugin.getLogger().info(player.getName() + " is not registered to users.json. Adding them.");
    }
    UserMapManager.putUserInMap(player.getName(), player.getUniqueId());

    User user = UserManager.getUser(player);
    user.setOnline(true);
    user.setVanish(ess.getUser(player).isVanished());

    if (MotdHandler.getMotd() != null) {
      // Send MOTD if it exists
      new MessageLaterTask(player, MotdHandler.getMotd()).runTaskLater(plugin, 20L);
    }
  }
}
