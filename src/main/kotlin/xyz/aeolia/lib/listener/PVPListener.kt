package xyz.aeolia.lib.listener

import hk.siggi.bukkit.plugcubebuildersin.world.WorldBlock
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import xyz.aeolia.lib.manager.UserManager
import xyz.aeolia.lib.sender.MessageSender
import xyz.aeolia.lib.serializable.User

class PVPListener(val plugin: JavaPlugin) : Listener {
  val playersWarned = mutableListOf<Player>()

  @EventHandler(priority = EventPriority.NORMAL)
  fun onPlayerQuit(event: PlayerQuitEvent) {
    playersWarned.remove(event.player)
    clearBlocks(UserManager.getUser(event.player))
  }

  @EventHandler(priority = EventPriority.LOWEST)
  fun onBlockPlace (event: BlockPlaceEvent) {
    val player = event.player
    val user = processBlockEvent(player, event.isCancelled, plugin) ?: return
    if (!user.modMode) {
      user.pvpBlocks.add(WorldBlock(event.block))
      globalBlocks.put(event.block.location, WorldBlock(event.block))
      if (player !in playersWarned) {
        MessageSender.sendMessage(player, "Blocks placed here will be deleted when you die or leave the world!")
        playersWarned.add(player)
      }
    } else {
      playersWarned.remove(player)
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  fun onBlockBreak(event: BlockBreakEvent) {
    val location = event.block.location
    val worldBlock = globalBlocks[location]?: return
    val user = worldBlock.placer?: return
    user.pvpBlocks.remove(worldBlock)
  }

  @EventHandler(priority = EventPriority.LOWEST)
  fun onPlayerWorldChange(event: PlayerChangedWorldEvent) {
    val player = event.player
    val from = event.from
    if (from.name==plugin.config.getString("pvp.world")) return
    clearBlocks(UserManager.getUser(player))
  }

  @EventHandler(priority = EventPriority.LOWEST)
  fun onPlayerDeath(event: PlayerDeathEvent) {
    val player = event.player
    val world = player.world
    if(world.name == plugin.config.getString("pvp.world")) return
    clearBlocks(UserManager.getUser(player))
  }

  companion object {
    val globalBlocks = mutableMapOf<(Location), WorldBlock>()

    fun clearBlocks(user: User) {
      user.pvpBlocks.forEach { block ->
        block.bukkitBlock.type = Material.AIR
        globalBlocks.remove(block.bukkitBlock.location)
      }
      user.pvpBlocks.clear()
    }

    fun processBlockEvent(player: Player, cancelled: Boolean, plugin: JavaPlugin): User? {
      val world = player.location.world
      if (world.name==plugin.config.getString("pvp.world")) {
        return null
      }
      if (cancelled) return null
      return UserManager.getUser(player)
    }

    fun tpPlayerToArena(player: Player, plugin: JavaPlugin) {
      val spawnLocations = plugin.config.getList("pvp.spawn-locations") ?: run {
        MessageSender.sendMessage(player, "pvp.spawn-locations doesn't exist! Please contact an administrator.")
        return
      }
      if (spawnLocations.isEmpty()) {
        plugin.logger.warning("PVP spawn labels are not configured!")
        return
      }
      val spawnLocation: Map<String, Double>
      try {
        @Suppress("UNCHECKED_CAST")
        spawnLocation = spawnLocations.random() as Map<String, Double>
      } catch(_: ClassCastException) {
        plugin.logger.warning("PVP spawn location could not be correctly cast")
        return
      }
      val location = Location(
            Bukkit.getServer().getWorld(plugin.config.getString("pvp.world")!!),
            spawnLocation["x"]?: (-100).toDouble(),
            spawnLocation["y"]?: (-100).toDouble(),
            spawnLocation["z"]?: (-100).toDouble()
        )
      player.teleport(location)
    }
  }
}