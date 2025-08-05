package xyz.aeolia.lib.manager

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.plugin.java.JavaPlugin
import xyz.aeolia.lib.data.User
import xyz.aeolia.lib.task.UserPruneTask
import java.io.File
import java.io.FileNotFoundException
import java.io.FileWriter
import java.io.IOException
import java.util.*
import java.util.concurrent.CompletableFuture

object UserManager {

  private val users: HashMap<UUID, User> = HashMap()
  private lateinit var plugin: JavaPlugin
  private lateinit var folder: File

  @JvmStatic
  fun init(plugin: JavaPlugin) {
    this.plugin = plugin
    folder = File(plugin.dataFolder, "/users/")
    if (!folder.exists()) {
      if (!folder.mkdir()) {
        plugin.logger.severe("Could not create directory ${folder.absolutePath}.")
        Bukkit.getPluginManager().disablePlugin(plugin)
      }
    }
  }

  @JvmStatic
  @JvmOverloads
  fun getUser(player: OfflinePlayer, recurse: Boolean = false): User {
    val uuid = player.uniqueId
    users[uuid]?.let { return it }

    val file = File(folder, "${uuid}.json")
    val user: User = run {
      if (!file.exists()) {
        return@run User(uuid = uuid, online = player.isOnline)
      }
      try {
        return@run Json.decodeFromString(file.readText())!!
      } catch (_: FileNotFoundException) {
        plugin.logger.severe("Could not read file ${file.absolutePath}.")
        return@run null
      }
    } ?: run { return User() }
    putUser(user)

    if (!user.online && !recurse) {
      UserPruneTask(player, plugin).runTaskLater(plugin, plugin.config.getLong("prune-time"))
    }

    return user
  }

  @JvmStatic
  fun putUser(user: User) {
    users.put(user.uuid!!, user)
  }

  @JvmStatic
  fun removeUser(player: OfflinePlayer) {
    val uuid = player.uniqueId
    saveUser(getUser(player, true)) // save user to prevent data loss from prune
    users.remove(uuid)
  }

  @JvmStatic
  fun saveUser(user: User) {
    if (user.uuid == null) return // Prevent data loss from saving malformed users
    val file = File(folder, user.uuid.toString() + ".json")
    try {
      FileWriter(file).use {
        it.write(Json.encodeToString(user))
        it.flush()
      }
    } catch (e: IOException) {
      throw RuntimeException(e)
    }
  }

  @JvmStatic
  fun saveUsers() {
    CompletableFuture.runAsync {
      saveUsersBlocking()
    }
  }

  fun saveUsersBlocking() {
    if (users.isEmpty()) {
      return
    }
    users.values.forEach { saveUser(it) }
    plugin.logger.info("Saved " + users.size + " users!")
  }
}