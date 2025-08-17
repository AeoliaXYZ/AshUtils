package xyz.aeolia.lib.manager

import net.luckperms.api.LuckPerms
import net.luckperms.api.model.user.User
import net.luckperms.api.node.Node
import net.luckperms.api.node.types.InheritanceNode
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.function.Consumer

object PermissionManager {
  lateinit var api: LuckPerms

  @JvmStatic
  fun groupUpdate(plugin: JavaPlugin, uuid: UUID, groupname: String, status: Boolean): Boolean {
    val userManager = api.userManager
    val userFuture = userManager.loadUser(uuid)

    userFuture.thenAcceptAsync(Consumer async@{ user: User ->
      if (status)
        user.data().add(Node.builder("group.$groupname").build())
      else {
        val group = api.groupManager.getGroup(groupname) ?: run {
          plugin.logger.warning("Group $groupname not found when trying to remove permission")
          return@async
        }
        user.data().remove(InheritanceNode.builder(group).build())
      }
      userManager.saveUser(user)
    })

    return true
  }

  @JvmStatic
  fun permissionUpdate(uuid: UUID, permission: String, status: Boolean): Boolean {
    val userManager = api.userManager
    val userFuture = userManager.loadUser(uuid)

    userFuture.thenAcceptAsync(Consumer { user: User ->
      if (status) user.data().add(Node.builder(permission).build())
      else user.data().remove(Node.builder(permission).build())
      userManager.saveUser(user)
    })

    return true
  }

  fun luckPermsSetup(): Boolean {
    val provider = Bukkit.getServicesManager().getRegistration(LuckPerms::class.java) ?: return false
    return true.also { api = provider.getProvider() }
  }
}