package xyz.aeolia.lib.manager;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class EconManager {
  private static Economy econ;

  public static boolean setupEconomy(JavaPlugin plugin) { // from Vault setup page
    if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
      return false;
    }
    RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
    if (rsp == null) {
      return false;
    }
    econ = rsp.getProvider();
    return true;
  }

  public static Economy getEcon() {
    return econ;
  }
}
