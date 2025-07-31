package xyz.aeolia.lib.command.user

import net.milkbowl.vault.economy.Economy
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.inventory.meta.Damageable
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.StringUtil
import xyz.aeolia.lib.manager.EconManager
import xyz.aeolia.lib.manager.RepairerManager
import xyz.aeolia.lib.sender.MessageSender
import xyz.aeolia.lib.utils.Message

class RepairTabExecutor(val plugin: JavaPlugin) : TabExecutor {
  val econ: Economy = EconManager.getEcon()

  override fun onTabComplete(
    sender: CommandSender,
    command: Command,
    label: String,
    args: Array<out String>
  ): List<String> {
    if (args.size != 1) return emptyList()
    val completions: MutableList<String> = ArrayList()
    val commands = listOf("all")
    StringUtil.copyPartialMatches<MutableList<String>>(args[0], commands, completions)
    return completions
  }

  override fun onCommand(
    sender: CommandSender,
    command: Command,
    label: String,
    args: Array<out String>
  ): Boolean {
    if (sender !is Player) return true.also { MessageSender.sendMessage(sender, Message.Generic.NOT_PLAYER) }

    var itemsRepaired = 0
    val itemsToRepair: MutableList<ItemStack> = ArrayList()
    when (args.size) {
      0 -> {
        val mainHandItem = sender.inventory.itemInMainHand
        if (mainHandItem.type == Material.AIR)
          return true.also { MessageSender.sendMessage(sender, Message.Generic.NOT_HOLDING) }
        if (mainHandItem.itemMeta !is Damageable)
          return true.also { MessageSender.sendMessage(sender, Message.Generic.INVALID_ITEM) }
        itemsToRepair.add(mainHandItem)
      }

      1 -> {
        sender.inventory.forEach {
          if (it.itemMeta is Damageable)
            itemsToRepair.add(it)
        }
        if (itemsToRepair.isEmpty())
          return true.also { MessageSender.sendMessage(sender, "You have nothing to repair.") }
      }

      else -> {
        MessageSender.sendMessage(sender, Message.Generic.TOO_MANY_ARGS)
        return false
      }
    }

    val repairerManager = RepairerManager(plugin, sender, itemsToRepair)
    if (repairerManager.costToRepair > econ.getBalance(sender))
      return true.also { MessageSender.sendMessage(sender, Message.Econ.INSUFFICIENT_FUNDS) }
    if (repairerManager.costToRepair == 0.0)
      return true.also { MessageSender.sendMessage(sender, "You have nothing to repair.") }
    repairerManager.repairers.forEach {
      val price = it.repair()
      if (price != 0.0) {
        itemsRepaired++
      }
    }
    return true.also { MessageSender.sendMessage(sender, "$itemsRepaired items repaired for " +
            "${plugin.config.getString("currency-symbol","A")}${"%.2f".format(repairerManager.costToRepair)}.") }
  }
}