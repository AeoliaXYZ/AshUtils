package xyz.aeolia.lib.command.user;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import xyz.aeolia.lib.manager.EconManager;
import xyz.aeolia.lib.sender.MessageSender;

import java.util.Objects;

import static xyz.aeolia.lib.utils.Message.Econ.SOLD;
import static xyz.aeolia.lib.utils.Message.Econ.TOO_MANY;
import static xyz.aeolia.lib.utils.Message.Generic.*;

public class HeadSellCommandExecutor implements CommandExecutor {
  JavaPlugin plugin;

  public HeadSellCommandExecutor(JavaPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
    if (sender instanceof Player player) {
      ItemStack itemInHand = player.getInventory().getItemInMainHand();
      if (itemInHand.getType() == Material.PLAYER_HEAD) {
        if (args.length != 1) {
          MessageSender.sendMessage(player, COMMAND_USAGE, true);
          return false;
        }

        String arg = args[0];
        int amountToSell;

        if (arg.equals("all")) {
          amountToSell = itemInHand.getAmount();
        } else {
          try {
            amountToSell = Integer.parseInt(arg);
            if (amountToSell > itemInHand.getAmount()) {
              MessageSender.sendMessage(player, TOO_MANY, true);
              return true;
            }
          } catch (NumberFormatException e) {
            MessageSender.sendMessage(player, COMMAND_USAGE, true);
            return false;
          }
        }

        int worth = this.plugin.getConfig().getInt("head-worth") * amountToSell;
        player.getInventory().getItemInMainHand().setAmount(itemInHand.getAmount() - amountToSell);
        Objects.requireNonNull(EconManager.getEcon()).depositPlayer(player, worth);
        MessageSender.sendMessage(player, String.format(SOLD, amountToSell, (amountToSell == 1 ? "head":"heads"),
                this.plugin.getConfig().getString("currency-symbol"), worth), true);
        return true;

      }
      MessageSender.sendMessage(player, INVALID_ITEM, true);
      return true;

    }
    MessageSender.sendMessage(sender, NOT_PLAYER, true);
    return true;
  }

}
