package xyz.aeolia.lib.sender

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import org.bukkit.plugin.java.JavaPlugin
import xyz.aeolia.lib.miniMessage

object MessageSender {
  lateinit var plugin: JavaPlugin

  @JvmStatic
  fun init(plugin: JavaPlugin) {
    this.plugin = plugin
  }

  @JvmStatic
  fun sendMessage(
    recipient: Audience,
    message: String?,
    includePrefix: Boolean = true
  ) {
    if (message.isNullOrEmpty()) return
    sendMessage(recipient, message.miniMessage(), includePrefix)

  }

  @JvmStatic
  fun sendMessage(recipient: Audience, message: Component, includePrefix: Boolean = true) {
    val toSend: Component
    if (includePrefix) {
      val prefix = (plugin.config.getString("chat-prefix") + " <reset>").miniMessage()
      toSend = Component.text()
        .append(prefix)
        .append(message)
        .build()
    } else toSend = message
    recipient.sendMessage(toSend)
  }
}