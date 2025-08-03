package xyz.aeolia.lib.command.user;

import org.apache.commons.text.RandomStringGenerator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import xyz.aeolia.lib.sender.MessageSender;
import xyz.aeolia.lib.sender.WebhookSender;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.Objects;

import static xyz.aeolia.lib.utils.Message.Error.GENERIC;
import static xyz.aeolia.lib.utils.Message.Generic.NOT_PLAYER;

public class CodeCommandExecutor implements CommandExecutor {
  JavaPlugin plugin;

  public CodeCommandExecutor(JavaPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
    if (!(sender instanceof Player player)) {
      MessageSender.sendMessage(sender, NOT_PLAYER, true);
      return true;
    }
    String code = new RandomStringGenerator.Builder()
            .withinRange('0', 'Z')
            .filteredBy(Character::isLetterOrDigit)
            .build()
            .generate(6);

    URI uri;
    try {
      uri = new URI(Objects.requireNonNull(plugin.getConfig().getString("discord.code-webhook")));
    } catch (URISyntaxException e) {
      plugin.getLogger().severe("discord.code-webhook URI invalid. Please check your config.");
      StringBuilder stackTrace = new StringBuilder();
      for (StackTraceElement element : e.getStackTrace()) {
        stackTrace.append(element.toString()).append("\n");
      }
      plugin.getLogger().severe(stackTrace.toString());
      MessageSender.sendMessage(sender, GENERIC, true);
      return true;
    }

    HttpResponse<String> response = WebhookSender.postWebhook(uri, player.getName() + "'s code is **" + code + "**. Only accept it within 24 hours of this message.");
    if (response.statusCode() != 204) {
      plugin.getLogger().severe("discord.code-webhook returned " + response.statusCode() + " " + response.body());
      MessageSender.sendMessage(sender, GENERIC, true);
    }

    MessageSender.sendMessage(sender, "Your code is <aqua>" + code
                    + "</aqua>. Staff may ask you for this code to verify you own your account. It is valid for 24 hours.",
            true);
    return true;
  }
}
