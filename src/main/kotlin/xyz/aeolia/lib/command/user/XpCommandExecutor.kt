package xyz.aeolia.lib.command.user

import net.milkbowl.vault.economy.Economy
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import xyz.aeolia.lib.manager.EconManager
import xyz.aeolia.lib.player
import xyz.aeolia.lib.sender.MessageSender
import xyz.aeolia.lib.utils.Experience
import xyz.aeolia.lib.utils.Message.Error.GENERIC
import xyz.aeolia.lib.utils.Message.Error.MISSING_DEPEND
import xyz.aeolia.lib.utils.Message.Generic.COMMAND_USAGE
import xyz.aeolia.lib.utils.Message.Generic.TOO_MANY_ARGS
import kotlin.math.floor

class XpCommandExecutor(var plugin: JavaPlugin) : CommandExecutor {
  val aqua: String = "<aqua>"
  val reset: String = "</aqua>"
  val econ: Economy? = EconManager.econ

  override fun onCommand(
    senderIn: CommandSender,
    command: Command,
    label: String,
    args: Array<String>
  ): Boolean {
    val sender = senderIn.player() ?: return true

    if (args.isEmpty()) {
      MessageSender.sendMessage(sender, COMMAND_USAGE, true)
      return true
    }
    if (args.size > 1) {
      MessageSender.sendMessage(sender, TOO_MANY_ARGS, true)
      return false
    }
    if (econ == null) {
      MessageSender.sendMessage(sender, MISSING_DEPEND.format("Economy"), true)
      return true
    }
    val experience = Experience(sender)

    when (command.name.lowercase()) {
      "xpbuy" -> return buyXp(sender, args[0], experience, experience.getTotalExperience())
      "xpsell" -> return sellXp(sender, args[0], experience, experience.getTotalExperience())
      else -> {
        plugin.logger.severe("Command ${command.name} not found in XpCommandExecutor! This is a bug.")
        MessageSender.sendMessage(sender, GENERIC, true)
        return false
      }
    }
  }

  fun buyXp(
    player: Player,
    arg: String,
    experience: Experience,
    playerCurrentXp: Int,
  ): Boolean {
    var xpToBuy: Int
    val playerBalance = econ!!.getBalance(player)
    val costPerXp = plugin.config.getDouble("xp.buy-cost")
    val maximumXpPurchasable = floor(playerBalance / costPerXp).toInt()

    if (costPerXp > playerBalance) {
      MessageSender.sendMessage(
        player, "You need at least ${formatCurrency(costPerXp)} to buy XP!", true
      )
      return true
    }

    xpToBuy = if (arg.equals("max", ignoreCase = true) || arg.equals("maximum", ignoreCase = true)) {
      maximumXpPurchasable
    } else try {
      arg.toInt()
    } catch (_: Exception) {
      MessageSender.sendMessage(player, COMMAND_USAGE, true)
      return false
    }
    val totalCost = costPerXp * xpToBuy
    if (totalCost > playerBalance) {
      MessageSender.sendMessage(
        player, ("You don't have enough money for that! You can buy a maximum of " +
                "$aqua$maximumXpPurchasable$reset XP."), true
      )
      return true
    }
    val xpMaximumBuy = plugin.config.getInt("xp.maximum-buy")
    if (xpToBuy > xpMaximumBuy && xpMaximumBuy > 0) {
      MessageSender.sendMessage(
        player, "This server limits the amount of XP you can buy per use of this command to " +
                "$aqua$xpMaximumBuy$reset.", true
      )
      xpToBuy = xpMaximumBuy
    }
    econ.withdrawPlayer(player, totalCost)
    experience.totalExperience = playerCurrentXp + xpToBuy

    MessageSender.sendMessage(
      player, "You have bought $aqua$xpToBuy XP $reset@ ${formatCurrency(costPerXp)}" +
              " per XP for ${formatCurrency(totalCost)}.", true
    )
    return true
  }

  fun sellXp(
    player: Player,
    arg: String,
    experienceManager: Experience,
    playerCurrentXp: Int,
  ): Boolean {
    if (playerCurrentXp == 0) {
      MessageSender.sendMessage(player, "You have no XP to sell!", true)
      return true
    }
    val xpToSell: Int
    if (arg.equals("all", ignoreCase = true)) {
      xpToSell = playerCurrentXp
    } else {
      try {
        xpToSell = arg.toInt()
      } catch (_: Exception) {
        MessageSender.sendMessage(player, COMMAND_USAGE, true)
        return false
      }
      if (xpToSell > playerCurrentXp) {
        MessageSender.sendMessage(
          player,
          "You don't have that much XP to sell! You can sell up to " +
                  "$aqua$playerCurrentXp$reset XP.", true
        )
        return true
      }
    }
    val worthPerXp = plugin.config.getDouble("xp.sell-worth")
    val totalWorth = worthPerXp * xpToSell
    experienceManager.totalExperience = playerCurrentXp - xpToSell
    econ!!.depositPlayer(player, totalWorth)
    MessageSender.sendMessage(
      player, "You have sold ${aqua}${xpToSell} XP ${reset}@ ${formatCurrency(worthPerXp)} " +
              "per XP for ${formatCurrency(totalWorth)}.", true
    )
    return true
  }

  private fun formatCurrency(value: Double): String {
    val currencySymbol: String = plugin.config.getString("currency-symbol")!!
    return "$aqua$currencySymbol${String.format("%.2f", value)}$reset"
  }
}