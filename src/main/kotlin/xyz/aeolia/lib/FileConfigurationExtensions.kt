package xyz.aeolia.lib

import org.bukkit.configuration.file.FileConfiguration

fun FileConfiguration.getListOfMutableMapStringDouble(path: String): List<MutableMap<String, Double>>? {
  val rawList = this.getList(path) ?: return null

  return rawList.mapIndexed { index, element ->
    val rawMap = element as? Map<*, *>
      ?: throw IllegalArgumentException("Element at $path[$index] is not a map: $element")

    rawMap.entries.associate { (key, value) ->
      val key = key as? String
        ?: throw IllegalArgumentException("Key at $path[$index] is not a string: $key")

      val value = when (value) {
        is Number -> value.toDouble()
        else -> throw IllegalArgumentException("Value for '$key' at $path[$index] is not a number: $value")
      }

      key to value
    }.toMutableMap()
  }
}