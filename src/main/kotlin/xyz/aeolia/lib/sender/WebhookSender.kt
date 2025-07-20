package xyz.aeolia.lib.sender

import com.google.gson.JsonObject
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import xyz.aeolia.lib.utils.Message.Error.CONFIG
import xyz.aeolia.lib.utils.Message.Error.REQUEST_FAIL
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class WebhookSender {
  companion object {
    lateinit var plugin : JavaPlugin

    fun init(plugin: JavaPlugin) {
      this.plugin = plugin
    }

    @JvmStatic
    fun postWebhook(uri: URI, content: String) : HttpResponse<String?> {
      val builder = HttpRequest.newBuilder(uri)
      val json = JsonObject()
      json.addProperty("content", content)

      builder.POST(HttpRequest.BodyPublishers.ofString(json.toString()))
      builder.headers("content-type", "application/json")
      val request = builder.build()
      return HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString()).join()
    }

    /*
    Send a Component to all users, then send the plaintext equivalent to the discord.broadcast-webhook.
     */
    fun broadcast(message: Component) : Boolean {
      MessageSender.sendMessage(Bukkit.getServer(), message, true)
      val messageAsString = PlainTextComponentSerializer.plainText().serialize(message)
      val uri = URI(plugin.config.getString("discord.broadcast-webhook") ?: run {
        plugin.logger.info(String.format(CONFIG, "discord.broadcast-webhook"))
        return false
      })
      val response = postWebhook(uri, messageAsString)
      if (response.statusCode() != 204) {
        plugin.logger.info(String.format(REQUEST_FAIL, response.statusCode(), response.body()))
        return false
      }
      return true
    }
  }
}