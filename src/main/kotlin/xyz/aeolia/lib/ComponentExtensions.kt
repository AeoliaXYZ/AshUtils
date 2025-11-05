package xyz.aeolia.lib

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer

fun Component.miniMessage(): String = miniMessage.serialize(this)

fun Component.plaintext(): String = PlainTextComponentSerializer.plainText().serialize(this)