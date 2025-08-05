package xyz.aeolia.lib

import net.kyori.adventure.text.Component

fun Component.miniMessage(): String = miniMessage.serialize(this)