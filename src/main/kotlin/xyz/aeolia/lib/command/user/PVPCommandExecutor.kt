package xyz.aeolia.lib.command.user

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import xyz.aeolia.lib.listener.PVPListener
import xyz.aeolia.lib.menu.PVPMenu
import xyz.aeolia.lib.player
import xyz.aeolia.lib.sender.MessageSender.sendMessage
import xyz.aeolia.lib.utils.Message

class PVPCommandExecutor(p: JavaPlugin) : CommandExecutor, PVPMenu(p) {
  override fun onCommand(
    senderIn: CommandSender,
    command: Command,
    label: String,
    args: Array<out String>
  ): Boolean {
    val sender = senderIn.player() ?: return true
    if (args.isEmpty() || (!sender.hasPermission("lib.pvp.manage"))) {
      inventory.open(sender)
      return true
    }

    if (args[0] == "respawn") {
      if (args.size == 1) {
        PVPListener.tpPlayerToArena(sender, plugin)
        return true
      }

      Bukkit.getOnlinePlayers().firstOrNull { it.name == args[1] }?.let {
        PVPListener.tpPlayerToArena(it, plugin)
      } ?: sendMessage(sender, Message.Player.NOT_FOUND)

      return true
    }
    if (args[0] == "addspawn") {
      val location = sender.location
      if (sender.world.name != plugin.config.getString("pvp.world")) {
        sendMessage(sender, "You can only run this command in the PVP world!")
        return true
      }
      val spawnLocations = plugin.config.getList("pvp.spawn-locations") ?: run {
        sendMessage(sender, Message.Error.CONFIG.format("pvp.spawn-locations"))
        return true
      }
      val locationMap = mutableMapOf<String, Double>()
      locationMap["x"] = location.blockX.toDouble() + 0.5 //Centre on block
      locationMap["y"] = location.blockY.toDouble()
      locationMap["z"] = location.blockZ.toDouble() + 0.5
      locationMap["yaw"] = location.yaw.toDouble()
      locationMap["pitch"] = location.pitch.toDouble()
      val newSpawnLocations = spawnLocations.plus(locationMap)
      plugin.config.set("pvp.spawn-locations", newSpawnLocations)
      plugin.saveConfig()
      plugin.reloadConfig()
      sendMessage(sender, "Location added.")
      return true
    }
    sendMessage(sender, Message.Generic.COMMAND_USAGE)
    return false
  }
}