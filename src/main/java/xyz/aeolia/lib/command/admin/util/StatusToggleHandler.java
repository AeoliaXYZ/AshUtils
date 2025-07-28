package xyz.aeolia.lib.command.admin.util;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.aeolia.lib.manager.StatusManager;
import xyz.aeolia.lib.sender.MessageSender;

import java.util.List;

import static xyz.aeolia.lib.utils.Message.Generic.COMMAND_USAGE;
import static xyz.aeolia.lib.utils.Message.Generic.TOO_MANY_ARGS;

public class StatusToggleHandler extends SubCommandHandler {
  final String instance;
  public StatusToggleHandler(String instance) {
    this.instance = switch (instance) {
      case "roe" -> "restartonempty";
      case "lc" -> "lockchat";
      default -> instance;
    };
  }

  @Override
  public boolean handle(@NotNull CommandSender sender, String @NotNull [] args) {
    String instanceFormatted = switch (instance) {
      case "restartonempty" -> "RestartOnEmpty";
      case "lockchat" -> "Chat lock";
      default -> "Unknown instance";
    };
    if (args.length > 0) {
      if (args.length > 1) {
        MessageSender.sendMessage(sender, TOO_MANY_ARGS, true);
        MessageSender.sendMessage(sender, "/util " + instance + " [true/false/status].", true);
      }
      switch (args[0].toLowerCase()) {
        case "true":
          StatusManager.setStatus(instance, true);
          MessageSender.sendMessage(sender, instanceFormatted + " enabled.", true);
          return true;
        case "false":
          StatusManager.setStatus(instance, false);
          MessageSender.sendMessage(sender, instanceFormatted + " disabled.", true);
          return true;
        case "status":
          MessageSender.sendMessage(sender,
                  instanceFormatted + " " + (StatusManager.getStatus(instance) ? "enabled" : "disabled") + ".",
                  true);
          return true;
        default:
          MessageSender.sendMessage(sender, COMMAND_USAGE, true);
          return false;
      }
    }
    boolean toSet = !StatusManager.getStatus(instance);
    StatusManager.setStatus(instance, toSet);
    MessageSender.sendMessage(sender, instanceFormatted + " is " + (toSet ? "enabled" : "disabled") + ".", true);
    return true;
  }

  @Override
  public @NotNull List<@NotNull String> getAlias() {
    return List.of("roe", "restartonempty", "lc", "lockchat");
  }
}

