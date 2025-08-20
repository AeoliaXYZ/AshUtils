package xyz.aeolia.lib.manager

import kotlinx.serialization.json.Json
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.UUID

object UserMapManager {
  private lateinit var plugin: JavaPlugin
  private lateinit var file: File

  @JvmStatic
  var userMap: MutableMap<String, String> = mutableMapOf()
  var modified = false

  private val json = Json { prettyPrint = true }

  fun init(plugin: JavaPlugin) {
    this.plugin = plugin
    this.file = File(plugin.dataFolder, "users.json")
    loadUserMap()
  }

  @JvmStatic
  fun loadUserMap() {
    if (!file.exists()) {
      file.writeText(json.encodeToString(userMap))
      return
    }
    userMap = json.decodeFromString<MutableMap<String, String>>(file.readText())
    plugin.logger.info("users.json loaded with ${userMap.size} users!")
  }

  @JvmStatic
  fun getUuidFromName(name: String): UUID? =
    userMap[name]?.let(UUID::fromString)

  @JvmStatic
  fun putUserInMap(name: String, uuid: UUID) {
    userMap[name] = uuid.toString()
    modified = true
  }

  @JvmStatic
  fun saveUserMap() {
    if (userMap.isEmpty() || !modified) return  // Prevent userMap from overwriting if the plugin crashes on startup
    file.writeText(json.encodeToString(userMap))
    plugin.logger.info("Saved users.json with ${userMap.size} values!")
    modified = false
  }
}
