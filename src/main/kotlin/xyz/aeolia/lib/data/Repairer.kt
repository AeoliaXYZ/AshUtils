package xyz.aeolia.lib.data

import net.milkbowl.vault.economy.Economy
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.ItemStack
import xyz.aeolia.lib.manager.EconManager
import xyz.aeolia.lib.manager.RepairerManager

class Repairer(val item: ItemStack, val player: Player, val rm: RepairerManager) {
  val econ: Economy? = EconManager.getEcon()

  val costToRepair: Double by lazy {
    if (damageTaken == 0) 0.0
    if (rm.chargePerItem) rm.repairCost
    else rm.repairCost * damageTaken
  }

  val damageTaken: Int by lazy {
    (item.itemMeta as? Damageable ?: return@lazy 0).damage
  }

  fun repair() : Double {
    item.itemMeta = ((item.itemMeta as? Damageable) ?: return 0.0).apply { damage = 0 }
    econ?.withdrawPlayer(player, costToRepair)
    return costToRepair
  }
}