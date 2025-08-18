package xyz.aeolia.lib.manager

import com.google.gson.Gson
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException
import java.util.function.Consumer
import java.util.function.Function

object UserMapManager {
  private lateinit var filePath: String

  @JvmStatic
  var userMap: MutableMap<String, String> = mutableMapOf()
  var modified = false

  private var gson: Gson = Gson()
  private lateinit var plugin: JavaPlugin

  fun init(plugin: JavaPlugin) {
    this.plugin = plugin
    this.filePath = plugin.dataFolder.toString() + "/users.json"
    loadUserMap()
  }

  @JvmStatic
  fun loadUserMap() {
    if (!File(filePath).exists()) {
      try {
        File(filePath).createNewFile()
        gson.toJson(HashMap<String, String>(), FileWriter(filePath))
      } catch (e: IOException) {
        throw RuntimeException(e)
      }
    } else {
      CompletableFuture.supplyAsync {
        try {
          val jsonContent = Files.readString(Paths.get(filePath))
          return@supplyAsync gson.fromJson(jsonContent, MutableMap::class.java)
        } catch (e: Exception) {
          throw RuntimeException(e)
        }
      }.thenAccept(
        Consumer { result: MutableMap<*, *> ->
          @Suppress("UNCHECKED_CAST")
          userMap = result as MutableMap<String, String>
          plugin.logger.info("users.json loaded with " + userMap.size + " users!")
        })
    }
  }

  @JvmStatic
  fun getUuidFromName(name: String): UUID? {
    if (!userMap.containsKey(name)) {
      return null
    }
    return UUID.fromString(userMap[name])
  }

  @JvmStatic
  fun putUserInMap(name: String, uuid: UUID) {
    userMap.put(name, uuid.toString())
    modified = true
  }

  @JvmStatic
  fun saveUserMap() {
    if (userMap.isEmpty()) return  // Prevent userMap from overwriting if the plugin crashes on startup
    if (!modified) return
    CompletableFuture.runAsync {
      try {
        FileWriter(filePath).use { writer ->
          gson.toJson(userMap, writer)
        }
      } catch (e: IOException) {
        throw CompletionException(e)
      }

    }.thenRun {
      plugin.logger.info("Saved users.json with " + userMap.size + " values!")
      modified = false

    }.exceptionally(Function { t: Throwable ->
        plugin.logger.severe("Failed to save users.json! Dumping it to console. Reason: " + t.message)
        plugin.logger.severe("StackTrace: " + t.stackTrace.contentToString())
        plugin.logger.severe("users.json: " + gson.toJson(userMap))
        null
      })
  }
}