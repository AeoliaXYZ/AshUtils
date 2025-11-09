package xyz.aeolia.lib.command.admin.util

import org.bukkit.command.CommandSender
import xyz.aeolia.lib.data.SubCommandHandler
import xyz.aeolia.lib.manager.StatusManager
import xyz.aeolia.lib.sender.MessageSender.sendMessage
import xyz.aeolia.lib.utils.Message.Generic.COMMAND_USAGE
import xyz.aeolia.lib.utils.Message.Generic.TOO_MANY_ARGS

class StatusToggleHandler(instance: String) : SubCommandHandler() {

  override val alias = listOf("roe", "restartonempty", "lc", "lockchat")

  val instance: String = when (instance) {
    "roe" -> "restartonempty"
    "lc" -> "lockchat"
    else -> instance
  }

  override fun handle(sender: CommandSender, args: Array<out String>): Boolean {
    val instanceFormatted = when (instance) {
      "restartonempty" -> "RestartOnEmpty"
      "lockchat" -> "Chat lock"
      else -> "Unknown instance"
    }

    if (args.isEmpty()) return true.also {
      val toSet = !StatusManager.getStatus(instance)
      StatusManager.setStatus(instance, toSet)
      sendMessage(
        sender,
        instanceFormatted + " is " + (if (toSet) "enabled" else "disabled") + ".", true
      )
    }

    if (args.size > 1) return true.also {
      sendMessage(sender, TOO_MANY_ARGS, true)
      sendMessage(sender, "/util $instance [true/false/status].", true)
    }

    when (args[0].lowercase()) {
      "true" -> return true.also {
        StatusManager.setStatus(instance, true)
        sendMessage(sender, "$instanceFormatted enabled.", true)
      }

      "false" -> return true.also {
        StatusManager.setStatus(instance, false)
        sendMessage(sender, "$instanceFormatted disabled.", true)
      }

      "status" -> return true.also {
        sendMessage(
          sender,
          instanceFormatted + " " + (if (StatusManager.getStatus(instance)) "enabled" else "disabled") + ".",
          true
        )
      }

      else -> return false.also { sendMessage(sender, COMMAND_USAGE, true) }
    }
  }
}