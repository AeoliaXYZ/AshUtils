package xyz.aeolia.lib

import cymru.asheiou.configmanager.ConfigManager
import org.bukkit.Bukkit
import org.bukkit.command.CommandExecutor
import org.bukkit.command.TabExecutor
import org.bukkit.plugin.java.JavaPlugin
import xyz.aeolia.lib.command.NotEnabledCommandExecutor
import xyz.aeolia.lib.command.PVPCommandExecutor
import xyz.aeolia.lib.command.admin.BroadcastCommandExecutor
import xyz.aeolia.lib.command.admin.FakeTabExecutor
import xyz.aeolia.lib.command.admin.MiniMessageCommandExecutor
import xyz.aeolia.lib.command.admin.ModCommandExecutor
import xyz.aeolia.lib.command.admin.VanishOnLoginTabExecutor
import xyz.aeolia.lib.command.admin.util.UtilTabExecutor
import xyz.aeolia.lib.command.user.*
import xyz.aeolia.lib.listener.BukkitEventListener
import xyz.aeolia.lib.listener.EssEventListener
import xyz.aeolia.lib.listener.MineListener
import xyz.aeolia.lib.listener.PVPListener
import xyz.aeolia.lib.listener.SuffixListener
import xyz.aeolia.lib.listener.VaultListener
import xyz.aeolia.lib.manager.EconManager
import xyz.aeolia.lib.manager.KitManager
import xyz.aeolia.lib.manager.PermissionManager
import xyz.aeolia.lib.manager.StatusManager
import xyz.aeolia.lib.manager.UserMapManager
import xyz.aeolia.lib.sender.MessageSender
import xyz.aeolia.lib.manager.UserManager
import xyz.aeolia.lib.sender.WebhookSender
import java.time.Duration
import java.time.Instant

class AeoliaLib : JavaPlugin() {
  override fun onEnable() {
    logger.info("Started load...")
    val startTime = Instant.now()
    val pm = server.pluginManager
    // Dependency check
    if (pm.getPlugin("Essentials") == null) {
      logger.info("No Essentials found!")
      pm.disablePlugin(this)
    }
    // Inits
    KitManager.init(this)
    MessageSender.init(this)
    UserManager.init(this)
    UserMapManager.loadUserMap()
    WebhookSender.init(this)
    val mineListener = MineListener(this)
    // Repeaters
    Bukkit.getScheduler().scheduleSyncRepeatingTask(this, { this.saveAll() }, 6000L, 6000L)
    Bukkit.getScheduler().scheduleSyncRepeatingTask(this, { mineListener.tick() }, 1L, 1L)
    pm.registerEvents(BukkitEventListener(this), this)
    pm.registerEvents(EssEventListener(this), this)
    pm.registerEvents(PVPListener(this), this)
    // Config
    ConfigManager(this, true).loadConfig()
    config.options().copyDefaults(true)
    saveConfig()
    // Commands
    val commands = HashMap<String, CommandExecutor>()
    commands["util"] = UtilTabExecutor(this)
    commands["broadcast"] = BroadcastCommandExecutor(this)
    commands["code"] = CodeCommandExecutor(this)
    commands["fake"] = FakeTabExecutor(this)
    commands["pvp"] = PVPCommandExecutor(this)
    commands["minimessage"] = MiniMessageCommandExecutor(this)
    commands["mod"] = ModCommandExecutor()
    commands["report"] = ReportCommandExecutor(this)
    // Softdepends
    //// LuckPerms
    if (pm.getPlugin("LuckPerms") != null) {
      PermissionManager.luckPermsSetup()
      commands["vanishonlogin"] = VanishOnLoginTabExecutor(this)
    } else {
      logger.info("LuckPerms not found - not enabling permission features.")
      commands["vanishonlogin"] = NotEnabledCommandExecutor()
    }
    //// SmartInvs
    if (pm.getPlugin("SmartInvs") != null && pm.getPlugin("LuckPerms") != null) {
      commands["suffix"] = SuffixCommandExecutor(this)
      pm.registerEvents(SuffixListener(this), this)
    } else {
      commands["suffix"] = NotEnabledCommandExecutor()
      logger.info("SmartInvs not found - not enabling /suffix.")
    }
    //// Vault
    if (EconManager.setupEconomy(this)) {
      pm.registerEvents(VaultListener(this), this)
      commands["headsell"] = HeadSellCommandExecutor(this)
      commands["xpsell"] = XpCommandExecutor(this)
      commands["xpbuy"] = XpCommandExecutor(this)
    } else {
      logger.info("Economy not found. Not enabling econ commands.")
      commands["headsell"] = NotEnabledCommandExecutor()
      commands["xpsell"] = NotEnabledCommandExecutor()
      commands["xpbuy"] = NotEnabledCommandExecutor()
    }
    // Commands
    for (command in commands) {
      setExecutor(command.key, command.value)
    }
    logger.info("Commands registered!")
    // Toggle
    StatusManager.setStatus("restartonempty", false)
    StatusManager.setStatus("lockchat", false)
    val endTime = Instant.now()
    logger.info("\u001B[32mLoad complete in " + Duration.between(startTime, endTime).toMillis() + "ms.\u001B[0m")
  }


  override fun onDisable() {
    saveAll()
    KitManager.cleanup()
  }

  fun saveAll() {
    UserMapManager.saveUserMap()
    UserManager.saveUsers()
  }

  fun setExecutor(command: String, executor: CommandExecutor?) {
    try {
      this.getCommand(command)?.setExecutor(executor)
      if (executor is TabExecutor) {
        this.getCommand(command)?.tabCompleter = executor
      }
    } catch (_: NullPointerException) {
      this.logger.warning("$command is not registered in the plugin.yml. Check your build.")
    }
  }
}