package xyz.aeolia.lib.listener;

import com.earth2me.essentials.Essentials;
import net.ess3.api.events.AfkStatusChangeEvent;
import net.ess3.api.events.VanishStatusChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.aeolia.lib.sender.WebhookSender;
import xyz.aeolia.lib.task.AFKTask;
import xyz.aeolia.lib.task.VanishTask;
import xyz.aeolia.lib.manager.UserManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EssEventListener implements Listener {
  JavaPlugin plugin;
  final Essentials ess;
  public EssEventListener(JavaPlugin plugin) {
    this.plugin = plugin;
    this.ess = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
    if (this.ess == null) {
      plugin.getLogger().severe("Essentials not found! This code has been initialised incorrectly.");
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onAfkStatusChange(AfkStatusChangeEvent event) {
    new AFKTask(event, plugin).runTaskLater(plugin, 2);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onVanishStatusChange(VanishStatusChangeEvent event) {
    new VanishTask(event).runTaskLater(plugin, 2);
  }
}
