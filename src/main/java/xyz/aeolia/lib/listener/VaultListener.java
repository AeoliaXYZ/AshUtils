package xyz.aeolia.lib.listener;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.aeolia.lib.data.User;
import xyz.aeolia.lib.manager.EconManager;
import xyz.aeolia.lib.manager.UserManager;
import xyz.aeolia.lib.sender.MessageSender;

import java.util.Objects;

public class VaultListener implements Listener {
  private final JavaPlugin plugin;

  public VaultListener(JavaPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onPlayerDeath(PlayerDeathEvent event) {
    FileConfiguration config = plugin.getConfig();
    Player victim = event.getEntity();
    if (!victim.getLocation().getWorld().getName().equals(plugin.getConfig().getString("pvp.world"))) {
      return;
    }
    for (ItemStack i : event.getDrops()) i.setAmount(0);
    Player killer = event.getEntity().getKiller();
    if (killer == null) {
      return;
    }
    User victimuser = UserManager.getUser(victim);
    long currentTimeSeconds = System.currentTimeMillis() / 1000L;
    if (currentTimeSeconds - victimuser.getLastPvpPayout() < config.getLong("pvp.prize-cooldown")) {
      return;
    }
    long pvpPrize = config.getLong("pvp.kill-prize");
    Objects.requireNonNull(EconManager.getEcon()).depositPlayer(killer, pvpPrize);
    victimuser.setLastPvpPayout(currentTimeSeconds);

    String messageEnd = " and claimed a prize of <aqua>"
            + config.getString("currency-symbol") + pvpPrize + "</aqua>.";
    MessageSender.sendMessage(victim, killer.getName() + " killed you" + messageEnd, true);
    MessageSender.sendMessage(killer, "You killed " + victim.getName() + messageEnd, true);
  }
}
