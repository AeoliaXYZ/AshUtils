package xyz.aeolia.lib.manager

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import xyz.aeolia.lib.data.Repairer

class RepairerManager(val plugin: JavaPlugin, val player: Player, val items: List<ItemStack>) {
  val repairers: MutableList<Repairer> = mutableListOf()
  var chargePerItem: Boolean = true
    private set
  var repairCost: Double = (-1).toDouble()
    private set

  fun init() : RepairerManager {
    chargePerItem = plugin.config.getBoolean("repair.charge-per-item", true)
    repairCost = plugin.config.getDouble("repair.cost")
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