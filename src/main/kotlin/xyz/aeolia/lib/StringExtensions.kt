package xyz.aeolia.lib

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

val miniMessage = MiniMessage.miniMessage()

fun String.miniMessage() : Component = miniMessage.deserialize(this)
