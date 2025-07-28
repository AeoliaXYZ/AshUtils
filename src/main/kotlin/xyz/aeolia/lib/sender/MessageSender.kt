package xyz.aeolia.lib.sender

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.plugin.java.JavaPlugin

object MessageSender {
  lateinit var miniMessage: MiniMessage
  lateinit var plugin: JavaPlugin

  @JvmStatic
  fun init(plugin: JavaPlugin) {
    this.plugin = plugin
    miniMessage = MiniMessage.builder()
      .tags(
        TagResolver.builder()
          .resolver(TagResolver.standard())
          .build()
      )
      .build()
  }

  @JvmStatic
  fun sendMessage(
    recipient: Audience,
    message: String?,
    includePrefix: Boolean = true
  ) {
    if (message.isNullOrEmpty()) return
    sendMessage(recipient, miniMessage.deserialize(message), includePrefix)

  }

  @JvmStatic
  fun sendMessage(recipient: Audience, message: Component, includePrefix: Boolean = true) {
    val toSend: Component
    if (includePrefix) {
      val prefix = miniMessage.deserialize(plugin.config.getString("chat-prefix") + " <reset>")
      toSend = Component.text()
        .append(prefix)
        .append(message)
        .build()
    } else toSend = message
    recipient.sendMessage(toSend)
  }
}