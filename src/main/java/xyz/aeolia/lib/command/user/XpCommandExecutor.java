package xyz.aeolia.lib.command.user;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import xyz.aeolia.lib.manager.EconManager;
import xyz.aeolia.lib.sender.MessageSender;
import xyz.aeolia.lib.utils.Experience;

import java.math.BigDecimal;

import static xyz.aeolia.lib.utils.Message.Error.GENERIC;
import static xyz.aeolia.lib.utils.Message.Generic.*;

public class XpCommandExecutor implements CommandExecutor {
  JavaPlugin plugin;
  String aqua = "<aqua>";
  String reset = "</aqua>";
  Economy econ = EconManager.getEcon();

  public XpCommandExecutor(JavaPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
    if (!(sender instanceof Player player)) {
      MessageSender.sendMessage(sender, NOT_PLAYER, true);
      return true;
    }
    if (args.length == 0) {
      MessageSender.sendMessage(sender, COMMAND_USAGE, true);
    }
    if (args.length > 1) {
      MessageSender.sendMessage(player, TOO_MANY_ARGS, true);
      return false;
    }
    String currencySymbol = plugin.getConfig().getString("currency-symbol");
    Experience experience = new Experience(player);

    switch (command.getName()) {
      case "xpbuy":
        return buyXp(player, args[0], experience, experience.getTotalExperience(), currencySymbol);
      case "xpsell":
        return sellXp(player, args[0], experience, experience.getTotalExperience(), currencySymbol);
      default:
        plugin.getLogger().severe("Command " + command.getName() + " not found in XpCommandExecutor! This is a bug.");
        MessageSender.sendMessage(player, GENERIC, true);
        return false;
    }
  }

  public boolean buyXp(Player player, String arg, Experience experienceManager, int playerCurrentXp, String currencySymbol) {
    int xpToBuy;
    double playerBalance = econ.getBalance(player);
    double costPerXp = plugin.getConfig().getDouble("xp.buy-cost");
    int maximumXpPurchasable = (int) Math.floor(playerBalance / costPerXp);

    if (costPerXp > playerBalance) {
      MessageSender.sendMessage(player, "You need at least " + aqua + currencySymbol + costPerXp + reset +
              " to buy XP!", true);
      return true;
    }

    if (arg.equalsIgnoreCase("max") || arg.equalsIgnoreCase("maximum")) {
      xpToBuy = maximumXpPurchasable;
    } else
      try {
        xpToBuy = Integer.parseInt(arg);
      } catch (Exception e) {
        MessageSender.sendMessage(player, COMMAND_USAGE, true);
        return false;
      }
    double totalCost = (BigDecimal.valueOf(costPerXp).multiply(BigDecimal.valueOf(xpToBuy))).doubleValue();
    if (totalCost > playerBalance) {
      MessageSender.sendMessage(player, "You don't have enough money for that! You can buy a maximum of " + aqua
              + maximumXpPurchasable + reset + " XP.", true);
      return true;
    }
    int xpMaximumBuy = plugin.getConfig().getInt("xp.maximum-buy");
    if (xpToBuy > xpMaximumBuy && xpMaximumBuy > 0) {
      MessageSender.sendMessage(player, "This server limits the amount of XP you can buy per use of this command to "
              + aqua + xpMaximumBuy + reset + ".", true);
      xpToBuy = xpMaximumBuy;
    }
    econ.withdrawPlayer(player, totalCost);
    experienceManager.setTotalExperience(playerCurrentXp + xpToBuy);

    MessageSender.sendMessage(player, "You have bought " + aqua + xpToBuy + " XP" + reset + "@ " + aqua + currencySymbol
            + costPerXp + reset + " per XP for " + aqua + currencySymbol + totalCost + reset + ".", true);
    return true;
  }

  public boolean sellXp(Player player, String arg, Experience experienceManager, int playerCurrentXp, String currencySymbol) {
    if (playerCurrentXp == 0) {
      MessageSender.sendMessage(player, "You have no XP to sell!", true);
      return true;
    }
    int xpToSell;
    if (arg.equalsIgnoreCase("all")) {
      xpToSell = playerCurrentXp;
    } else {
      try {
        xpToSell = Integer.parseInt(arg);
      } catch (Exception e) {
        MessageSender.sendMessage(player, COMMAND_USAGE, true);
        return false;
      }
      if (xpToSell > playerCurrentXp) {
        MessageSender.sendMessage(player,
                "You don't have that much XP to sell! You can sell up to " + aqua + playerCurrentXp + reset +
                        " XP.", true);
        return true;
      }
    }
    double worthPerXp = plugin.getConfig().getDouble("xp.sell-worth");
    double totalWorth = (BigDecimal.valueOf(worthPerXp).multiply(BigDecimal.valueOf(xpToSell))).doubleValue();
    experienceManager.setTotalExperience(playerCurrentXp - xpToSell);
    econ.depositPlayer(player, totalWorth);
    MessageSender.sendMessage(player, "You have sold " + aqua + xpToSell + " XP " + reset + "@ " + aqua + currencySymbol
            + worthPerXp + reset + " per XP for " + aqua + currencySymbol + totalWorth + reset + ".", true);
    return true;
  }
}
