package xyz.aeolia.lib.command.user.suffix

import net.luckperms.api.model.group.Group
import net.luckperms.api.node.Node
import net.luckperms.api.node.types.SuffixNode
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import xyz.aeolia.lib.data.SubCommandHandler
import xyz.aeolia.lib.manager.PermissionManager
import xyz.aeolia.lib.menu.SuffixMenu
import xyz.aeolia.lib.sender.MessageSender
import java.util.function.Consumer

class SuffixCreateHandler(private val plugin: JavaPlugin) : SubCommandHandler() {
  override val alias = listOf("create")

  override fun handle(sender: CommandSender, args: Array<out String>): Boolean {
    val suffixList = plugin.config.getStringList("suffix.list")

    if (!sender.hasPermission("lib.suffix-create")) return true

    val formatted = SuffixMenu.formatSuffix(args[0], true)

    PermissionManager.api.groupManager.createAndLoadGroup(args[0])
      .thenAccept(Consumer { e: Group ->
        val formattedAmpersand = SuffixMenu.formatSuffix(args[0], false)
        val node: Node = SuffixNode.builder()
          .priority(5)
          .suffix(" $formattedAmpersand")
          .build()
        e.data().add(node)

        PermissionManager.api.groupManager.saveGroup(e)
        if (!suffixList.contains(args[0])) {
          suffixList.add(args[0])
          plugin.config.set("suffix.list", suffixList)
          plugin.saveConfig()
        }
        MessageSender.sendMessage(sender, "Created suffix $formatted<reset>.")

      }).exceptionally { throwable ->
        plugin.logger.severe("Could not create group!")
        MessageSender.sendMessage(sender, throwable.message)
        null
      }
    return true
  }

}