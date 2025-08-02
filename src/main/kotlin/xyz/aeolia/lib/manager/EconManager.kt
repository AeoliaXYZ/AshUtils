package xyz.aeolia.lib.manager

import net.milkbowl.vault.economy.Economy
import org.bukkit.plugin.java.JavaPlugin

object EconManager {

  @JvmStatic
  var econ: Economy? = null
    private set

  @JvmStatic
  fun setupEconomy(plugin: JavaPlugin): Boolean {
    plugin.server.pluginManager.getPlugin("Vault") ?: return false
    econ = (plugin.server.servicesManager.getRegistration(Economy::class.java) ?: return false).provider
    return true
  }
}