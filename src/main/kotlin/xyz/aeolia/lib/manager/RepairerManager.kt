package xyz.aeolia.lib.manager

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import xyz.aeolia.lib.data.Repairer

class RepairerManager(
  val plugin: JavaPlugin,
  val player: Player,
  val items: List<ItemStack>,
  val chargePerItem : Boolean =  plugin.config.getBoolean("repair.charge-per-item", true),
  val repairCost : Double = plugin.config.getDouble("repair.cost", 0.0)) {
  val repairers: MutableList<Repairer> = mutableListOf()

  fun init() : RepairerManager {
    for (item in items) {
      repairers.add(Repairer(item, player, this))
    }
    return this
  }

  val costToRepair by lazy {
    var runningCost = 0.0
    repairers.forEach {
      runningCost += it.costToRepair
    }
    runningCost
  }
}