package xyz.aeolia.lib.listener;

import com.earth2me.essentials.Essentials;
import net.ess3.api.events.AfkStatusChangeEvent;
import net.ess3.api.events.VanishStatusChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.aeolia.lib.task.AFKTask;
import xyz.aeolia.lib.task.VanishStatusChangeTask;

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
    new VanishStatusChangeTask(event).runTaskLater(plugin, 2);
  }
}
