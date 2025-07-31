package xyz.aeolia.lib.utils

object Message {
  object Econ {
    const val SOLD = "You have sold <aqua>%d<reset> %s for <aqua>%s%d<reset>."
    const val TOO_MANY = "You don't have that many to sell!"
    const val INSUFFICIENT_FUNDS = "You don't have enough money!"
  }
  object Error {
    const val CONFIG = "The config is not correctly configured - it is missing field %s."
    const val GENERIC = "An internal error occurred. Please contact an administrator."
    const val REQUEST_FAIL = "The HTTP request failed with code %s. Response: %s."
    const val REQUEST_FAIL_GENERIC = "The HTTP request failed. Check console for more details."
    const val TELEPORT_FAIL = "Teleportation failed. Please contact an administrator."
  }
  object Generic {
    const val COMMAND_USAGE = "Unrecognised usage. Usage:"
    const val INVALID_ITEM = "The item you're holding is invalid for this command."
    const val NOT_HOLDING = "You're not holding anything!"
    const val NOT_PLAYER = "You must be a player to execute this command."
    const val NOT_PLAYER_ARGS = "You must be a player to execute this command without arguments."
    const val TOO_MANY_ARGS = "Too many arguments! Usage:"
  }
  object Player {
    const val NOT_FOUND = "Player not found. Please check your spelling and try again."
    const val OFFLINE = "This command cannot accept an offline player."
  }
}