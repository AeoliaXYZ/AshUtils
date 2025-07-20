package xyz.aeolia.lib.data

import kotlinx.serialization.Serializable

@Serializable
data class Kit(
  var id: String? = null,
  val displayName: String,
  val permission: String = "lib.kit",
  val items: List<Item>,
  val displayItem: Item,
)