package xyz.aeolia.lib.command.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.aeolia.lib.manager.PermissionManager;
import xyz.aeolia.lib.manager.UserMapManager;
import xyz.aeolia.lib.sender.MessageSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static xyz.aeolia.lib.utils.Message.Generic.*;
import static xyz.aeolia.lib.utils.Message.Player.NOT_FOUND;

public class VanishOnLoginTabExecutor implements TabExecutor {
  private final JavaPlugin plugin;

  public VanishOnLoginTabExecutor(JavaPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  @SuppressWarnings("deprecated")
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
    switch (args.length) {
      case 0:
        if (sender instanceof Player player) {
          return permissionUpdate(sender, player.getUniqueId(), player.getName(), !player.hasPermission("group." + plugin.getConfig().getString("vanish-on-login-group")));
        }
        MessageSender.sendMessage(sender, NOT_PLAYER_ARGS, true);
        return true;
      case 1:
        if (sender instanceof Player player) {
          return switch (args[0]) {
            case "true" -> permissionUpdate(sender, player.getUniqueId(), player.getName(), true);
            case "false" -> permissionUpdate(sender, player.getUniqueId(), player.getName(), false);
            default -> {
              MessageSender.sendMessage(sender, COMMAND_USAGE, true);
              yield false;
            }
          };
        }
      case 2:
        if (sender.hasPermission("lib.vanishonlogin.others")) {
          UUID playerUUID = UserMapManager.getUuidFromName(args[1]);
          if (playerUUID == null) {
            MessageSender.sendMessage(sender, NOT_FOUND, true);
            return true;
          }
          return switch (args[0]) {
            case "true" -> permissionUpdate(sender, playerUUID, args[1], true);
            case "false" -> permissionUpdate(sender, playerUUID, args[1], false);
            default -> {
              MessageSender.sendMessage(sender, COMMAND_USAGE, true);
              yield false;
            }
          };
        }
      default:
        MessageSender.sendMessage(sender, TOO_MANY_ARGS, true);
        return false;
    }
  }

  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
    List<String> completions = new ArrayList<>();
    java.util.List<String> commands = new ArrayList<>();

    if (args.length == 1) {
      if (sender.hasPermission("lib.vanishonlogin")) {
        commands.add("true");
        commands.add("false");
      }
      StringUtil.copyPartialMatches(args[0], commands, completions);

    } else if (args.length == 2) {
      if (args[0].equals("true") || args[0].equals("false")) {
        if (sender.hasPermission("lib.vanishonlogin.others")) {
          for (Player player : Bukkit.getOnlinePlayers()) {
            commands.add(player.getName());
          }
        }
        StringUtil.copyPartialMatches(args[1], commands, completions);
      }
    }
    Collections.sort(completions);
    return completions;
  }

  boolean permissionUpdate(CommandSender sender, UUID uuid, String playerName, boolean status) {
    PermissionManager.groupUpdate(plugin, uuid, plugin.getConfig().getString("vanish-on-login-group", ""), status);
    String toSend = "VanishOnLogin " + (status ? "enabled" : "disabled") + " for " + playerName + ".";
    MessageSender.sendMessage(sender, toSend, true);
    return true;
  }
}
