package xyz.aeolia.lib.command.user

import net.milkbowl.vault.economy.Economy
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.StringUtil
import xyz.aeolia.lib.manager.EconManager
import xyz.aeolia.lib.manager.RepairerManager
import xyz.aeolia.lib.sender.MessageSender.sendMessage
import xyz.aeolia.lib.utils.Message

class RepairTabExecutor(val plugin: JavaPlugin) : TabExecutor {
  val econ: Economy? = EconManager.econ

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
    if (sender !is Player) return true.also { sendMessage(sender, Message.Generic.NOT_PLAYER) }

    var itemsRepaired = 0
    val itemsToRepair: MutableList<ItemStack> = ArrayList()
    when (args.size) {
      0 -> {
        val mainHandItem = sender.inventory.itemInMainHand
        if (mainHandItem.type == Material.AIR)
          return true.also { sendMessage(sender, Message.Generic.NOT_HOLDING) }
        if (mainHandItem.itemMeta !is Damageable)
          return true.also { sendMessage(sender, Message.Generic.INVALID_ITEM) }
        itemsToRepair.add(mainHandItem)
      }

      1 -> {
        sender.inventory.forEach {
          if (it.type == Material.AIR) return@forEach
          if (it.itemMeta is Damageable && it.type.maxDurability > 0 && (it as Damageable).damage > 0)
            itemsToRepair.add(it)
        }
        if (itemsToRepair.isEmpty())
          return true.also { sendMessage(sender, "You have nothing to repair.") }
      }

      else ->
        return false.also { sendMessage(sender, Message.Generic.TOO_MANY_ARGS) }
    }

    val repairerManager = RepairerManager(plugin, sender, itemsToRepair)
    if (repairerManager.costToRepair > (econ ?: run {
        return true.also { sendMessage(sender, Message.Error.MISSING_DEPEND.format("Economy")) }
      }).getBalance(sender))
      return true.also { sendMessage(sender, Message.Econ.INSUFFICIENT_FUNDS) }
    if (repairerManager.costToRepair == 0.0)
      return true.also { sendMessage(sender, "You have nothing to repair.") }
    repairerManager.repairers.forEach {
      val price = it.repair()
      if (price != 0.0) {
        itemsRepaired++
      }
    }
    return true.also {
      sendMessage(
        sender, "$itemsRepaired item${
          if (itemsRepaired == 1) "" else "s"
        } repaired for ${plugin.config.getString("currency-symbol", "A")}" +
                "${"%.2f".format(repairerManager.costToRepair)}."
      )
    }
  }
}