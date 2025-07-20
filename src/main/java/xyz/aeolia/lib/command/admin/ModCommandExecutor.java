package xyz.aeolia.lib.command.admin;

import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.aeolia.lib.data.User;
import xyz.aeolia.lib.manager.UserManager;
import xyz.aeolia.lib.sender.MessageSender;

import static xyz.aeolia.lib.utils.Message.Generic.COMMAND_USAGE;
import static xyz.aeolia.lib.utils.Message.Generic.NOT_PLAYER;

public class ModCommandExecutor implements CommandExecutor {
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
    if (!(sender instanceof Player player)) {
      MessageSender.sendMessage(sender, NOT_PLAYER, true);
      return true;
    }
    User user = UserManager.getUser(player);
    boolean toSet;
    if (args.length == 0) {
      toSet = !user.getModMode();
    } else switch (args[0]) {
      case "true":
        toSet = true;
        break;
      case "false":
        toSet = false;
        break;
      case "status":
        MessageSender.sendMessage(sender, "Mod mode is currently " + (user.getModMode()
                ? "enabled." : "disabled."), true);
        return true;
      default:
        MessageSender.sendMessage(sender, COMMAND_USAGE, true);
        return false;
    }
    user.setModMode(toSet);
    MessageSender.sendMessage(sender, "Mod mode " + (toSet ? "enabled." : "disabled."), true);

    try {
      /* Soft depend ess */
      Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("essentials");
      assert ess != null;
      ess.getUser(player).setSocialSpyEnabled(toSet);
    } catch (NoClassDefFoundError ignored) {
    }

    return true;
  }
}
