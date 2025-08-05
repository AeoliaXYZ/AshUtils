package xyz.aeolia.lib.command.user

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import xyz.aeolia.lib.sender.MessageSender
import xyz.aeolia.lib.sender.WebhookSender
import xyz.aeolia.lib.utils.Message.Error.GENERIC
import xyz.aeolia.lib.utils.Message.Generic.COMMAND_USAGE
import xyz.aeolia.lib.utils.Message.Generic.NOT_PLAYER
import java.lang.String
import java.net.URI
import kotlin.Array
import kotlin.Boolean
import kotlin.Exception

class ReportCommandExecutor(var plugin: JavaPlugin) : CommandExecutor {
  override fun onCommand(
    sender: CommandSender,
    command: Command,
    label: String,
    args: Array<out String>
  ): Boolean {
    if (sender !is Player) {
      MessageSender.sendMessage(sender, NOT_PLAYER, true)
      return true
    }

    if (args.isEmpty()) {
      MessageSender.sendMessage(sender, COMMAND_USAGE, true)
      return false
    }
    val uri: URI?
    try {
      uri = URI((plugin.config.getString("discord.report-webhook")!!))
    } catch (e: Exception) {
      plugin.logger.severe("Invalid discord.report-webhook in config.yml.")
      e.printStackTrace()
      MessageSender.sendMessage(sender, GENERIC, true)
      return true
    }
    val location = sender.location
    val response = WebhookSender.Companion.postWebhook(
      uri, sender.name + " has created a report at " +
              location.blockX + ", " + location.blockY + ", " + location.blockZ + " in " +
              location.world?.name + ":\n" + String.join(" ", *args)
    )

    if (response.statusCode() != 204) {
      plugin.logger.severe("Could not send report: " + response.statusCode() + " " + response.body())
      MessageSender.sendMessage(sender, GENERIC, true)
      return true
    }
    MessageSender.sendMessage(
      sender, "Report sent successfully. You will receive a response via " +
              "<aqua>/mail</aqua> or on our Discord if you're a member.", true
    )
    return true
  }
}