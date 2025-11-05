package xyz.aeolia.lib.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import xyz.aeolia.lib.utils.UUIDSerializer
import java.util.*

@Serializable
data class User(
  @Transient var online: Boolean = false,
  @Transient var pvpBlocks: MutableList<LibBlock> = mutableListOf(),
  @SerialName("lastpvppayout") var lastPvpPayout: Long = 0,
  @SerialName("modmode") var modMode: Boolean = false,
  @Serializable(with = UUIDSerializer::class) var uuid: UUID? = null,
  var vanish: Boolean = false,
 ) {
  fun getData() : Map<String, Any?> = mapOf(
    "online" to online,
    "pvpblocks" to pvpBlocks,
    "lastpvppayout" to lastPvpPayout,
    "modmode" to modMode,
    "uuid" to uuid,
    "vanish" to vanish
  )
}