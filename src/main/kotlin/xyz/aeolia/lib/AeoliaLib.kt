package xyz.aeolia.lib

import cymru.asheiou.configmanager.ConfigManager
import org.bukkit.Bukkit
import org.bukkit.command.CommandExecutor
import org.bukkit.command.TabExecutor
import org.bukkit.plugin.java.JavaPlugin
import xyz.aeolia.lib.command.NotEnabledCommandExecutor
import xyz.aeolia.lib.command.admin.*
import xyz.aeolia.lib.command.admin.util.UtilTabExecutor
import xyz.aeolia.lib.command.user.*
import xyz.aeolia.lib.listener.*
import xyz.aeolia.lib.manager.*
import xyz.aeolia.lib.sender.MessageSender
import xyz.aeolia.lib.sender.WebhookSender
import java.time.Duration
import java.time.Instant

open class AeoliaLib : JavaPlugin() {
  val configManager = ConfigManager(this, true)
  var registeredCommands = 0

  override fun onEnable() {
    logger.info("Started load...")
    val startTime = Instant.now()
    val pm = server.pluginManager
    val notEnabled = NotEnabledCommandExecutor()
    // Dependency check
    try {
      Class.forName("kotlin.Unit")
      logger.info("Kotlin stdlib found!")
    } catch (_: ClassNotFoundException) {
      logger.info("Kotlin stdlib not found! Disabling plugin...")
      pm.disablePlugin(this)
    }
    if (pm.getPlugin("Essentials") == null) {
      logger.info("No Essentials found!")
      pm.disablePlugin(this)
    }
    // Inits
    EnchantmentManager.init(this)
    KitManager.init(this)
    MessageSender.init(this)
    UserManager.init(this)
    UserMapManager.init(this)
    WebhookSender.init(this)
    val mineListener = MineListener(this)
    // Repeaters
    Bukkit.getScheduler().scheduleSyncRepeatingTask(this, { this.saveAll() }, 6000L, 6000L)
    Bukkit.getScheduler().scheduleSyncRepeatingTask(this, { mineListener.tick() }, 1L, 1L)
    pm.registerEvents(BukkitEventListener(this), this)
    pm.registerEvents(EssEventListener(this), this)
    pm.registerEvents(PVPListener(this), this)
    // Config
    configManager.loadConfig()
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
      commands["vanishonlogin"] = notEnabled
    }
    //// SmartInvs
    if (pm.getPlugin("SmartInvs") != null && pm.getPlugin("LuckPerms") != null) {
      commands["suffix"] = SuffixCommandExecutor(this)
      pm.registerEvents(SuffixListener(this), this)
    } else {
      commands["suffix"] = notEnabled
      logger.info("SmartInvs not found - not enabling /suffix.")
    }
    //// Vault
    if (EconManager.setupEconomy(this)) {
      pm.registerEvents(VaultListener(this), this)
      commands["repair"] = RepairTabExecutor(this)
      commands["enchant"] = EnchantTabExecutor(this)
      commands["headsell"] = HeadSellCommandExecutor(this)
      commands["xpsell"] = XpCommandExecutor(this)
      commands["xpbuy"] = XpCommandExecutor(this)
    } else {
      logger.info("Economy not found. Not enabling econ commands.")
      commands["enchant"] = notEnabled
      commands["repair"] = notEnabled
      commands["headsell"] = notEnabled
      commands["xpsell"] = notEnabled
      commands["xpbuy"] = notEnabled
    }
    // Commands
    for (command in commands) {
      setExecutor(command.key, command.value)
    }
    logger.info("Commands registered: $registeredCommands")
    // Toggle
    StatusManager.setStatus("restartonempty", false)
    StatusManager.setStatus("lockchat", false)
    val endTime = Instant.now()
    logger.info("\u001B[32mLoad complete in " + Duration.between(startTime, endTime).toMillis() + "ms.\u001B[0m")
  }


  override fun onDisable() {
    Bukkit.getScheduler().cancelTasks(this)
    UserMapManager.saveUserMap()
    UserManager.saveUsersBlocking()
  }

  fun saveAll() {
    UserMapManager.saveUserMap()
    UserManager.saveUsers()
  }

  fun setExecutor(command: String, executor: CommandExecutor) {
    try {
      this.getCommand(command)?.apply {
        setExecutor(executor)
        registeredCommands++
        if (executor is TabExecutor) {
          tabCompleter = executor
        }
      }
    } catch (_: NullPointerException) {
      this.logger.warning("$command is not registered in the plugin.yml. Check your build.")
    }
  }
}