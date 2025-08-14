package xyz.aeolia.lib.task

import net.ess3.api.events.AfkStatusChangeEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import xyz.aeolia.lib.manager.UserManager.getUser
import xyz.aeolia.lib.sender.WebhookSender.Companion.postWebhook
import java.net.URI
import java.net.URISyntaxException
import java.util.*

class AFKTask(val event: AfkStatusChangeEvent, val plugin: JavaPlugin) : BukkitRunnable() {
  override fun run() {
    val player = event.getAffected().base
    if (player.user().vanish) return
    if (!player.isOnline) return
    val clean = event.affected.displayName.replace("§.".toRegex(), "")
    val uri: URI
    try {
      uri = URI(Objects.requireNonNull<String>(plugin.config.getString("discord.afk-webhook")))
    } catch (_: URISyntaxException) {
      plugin.logger.severe("Invalid discord.afk-webhook URI!")
      return
    }

    val response = postWebhook(uri, "**" + clean + "** is " + (if (event.value) "now" else "no longer") + " AFK.")

    if (response.statusCode() < 200 || response.statusCode() >= 300) {
      plugin.logger.severe("afk-webhook returned: " + response.statusCode() + " " + response.body())
    }
  }
}