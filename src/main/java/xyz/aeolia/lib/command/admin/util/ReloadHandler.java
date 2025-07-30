package xyz.aeolia.lib.command.admin.util;

import cymru.asheiou.configmanager.ConfigManager;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import xyz.aeolia.lib.manager.EnchantmentManager;
import xyz.aeolia.lib.manager.KitManager;
import xyz.aeolia.lib.sender.MessageSender;

import java.util.List;

public class ReloadHandler extends SubCommandHandler {
  private final JavaPlugin plugin;
  public ReloadHandler(JavaPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public @NotNull List<String> getAlias() {
    return List.of("reload");
  }

  @Override
  public boolean handle(@NotNull CommandSender sender, String @NotNull [] args) {
    MessageSender.sendMessage(sender, "Starting config reload...", true);
    Integer[] response = new ConfigManager(plugin, true).loadConfig();
    String compose = "Reload complete! ";
    if (response[0] == -1) {
      compose += "Your config file was empty, deleted, or unreadable. It has been replaced with the default.";
    } else if (response[0] > 0 || response[1] > 0) {
      compose += "Added " + response[0] + " value" + (response[0] == 1 ? "" : "s") + ", removed "
              + response[1] + " value" + (response[1] == 1 ? "" : "s") + ".";
    }
    MessageSender.sendMessage(sender, compose, true);
    MessageSender.sendMessage(sender, "Starting kits reload...", true);
    KitManager.init(plugin, sender);
    MessageSender.sendMessage(sender, "Starting enchants reload...", true);
    EnchantmentManager.INSTANCE.reloadEnchantments();
    return true;
  }
}
