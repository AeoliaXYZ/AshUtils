package xyz.aeolia.lib.command.user

import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.StringUtil
import xyz.aeolia.lib.data.EnchantResult
import xyz.aeolia.lib.manager.EnchantmentManager
import xyz.aeolia.lib.player
import xyz.aeolia.lib.sender.MessageSender
import xyz.aeolia.lib.utils.Message

class EnchantTabExecutor(val plugin: JavaPlugin) : TabExecutor {
  override fun onTabComplete(
    sender: CommandSender,
    command: Command,
    label: String,
    args: Array<out String>
  ): List<String>? {
    val completions: MutableList<String> = ArrayList()
    val commands: MutableList<String> = ArrayList()

    if (args.size == 1) {
      EnchantmentManager.enchantmentStrings.forEach {
        commands.add(it.key)
      }
    } else if (args.size == 2) {
      if (args[0] in EnchantmentManager.enchantmentStrings.keys) {
        var i = 0
        EnchantmentManager.enchantmentStrings[args[0]]!!.forEach { _ ->
          commands.add((i+1).toString()).also { i++ }
        }
      }
    }

    StringUtil.copyPartialMatches<MutableList<String>>(args[0], commands, completions)
    completions.sort()
    return completions
  }

  override fun onCommand(
    senderIn: CommandSender,
    command: Command,
    label: String,
    argsIn: Array<out String>
  ): Boolean {
    val args = mutableListOf<String>()
    args.addAll(argsIn)
    val sender = senderIn.player() ?: return true
    if(args.isEmpty() || args.size > 2) return false.also { MessageSender.sendMessage(sender, Message.Generic.COMMAND_USAGE) }
    val enchant = EnchantmentManager.nameToEnchant(args[0]) ?: run {
      return true.also { MessageSender.sendMessage(sender, "That enchantment was not found!") }
    }

    if (args.size == 1) {
      val maxLevel = EnchantmentManager.enchantments[enchant]!!.size
      args.add(maxLevel.toString())
    }

    val currencySymbol = plugin.config.getString("currency-symbol")

    if(args[1] == "price") {
      var i = 1
      var message = "<aqua>Prices for ${args[0]}:</aqua>"
      EnchantmentManager.enchantments[enchant]!!.forEach {
        message += "\n$i: $currencySymbol$it"
        i++
      }
      return true.also { MessageSender.sendMessage(sender, message, false) }
    }
    val level = try {
      Integer.parseInt(args[1])
    } catch (_: NumberFormatException) {
      return false.also { MessageSender.sendMessage(sender, "Not a valid number for the enchantment level. Usage:") }
    }
    val item = sender.inventory.itemInMainHand
    if (item.type == Material.AIR) return true.also { MessageSender.sendMessage(sender, "You're not holding anything!") }
    val result = EnchantmentManager.addAndDebit(enchant, level, item, sender)
    val message = when (result.first) {
      EnchantResult.SUCCESS -> "Enchantment added successfully! You have been charged $currencySymbol${result.second}."
      EnchantResult.INVALID_LEVEL -> "The level you provided is out of bounds for that enchantment."
      EnchantResult.CONFLICTING_ENCHANTMENTS -> "That enchantment conflicts with an existing enchantment on this tool."
      EnchantResult.INCOMPATIBLE_ENCHANTMENT -> "That enchantment is not compatible with this item."
      EnchantResult.INVALID_ENCHANTMENT -> "That enchantment was not found!"
      EnchantResult.INSUFFICIENT_FUNDS -> "You don't have enough money to purchase that enchantment."
      EnchantResult.MISSING_DEPENDENCY -> Message.Error.MISSING_DEPEND.format("Economy")
    }
    return true.also { MessageSender.sendMessage(sender, message) }
  }
}