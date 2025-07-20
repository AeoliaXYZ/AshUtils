package xyz.aeolia.lib.serializable

import hk.siggi.bukkit.plugcubebuildersin.world.WorldBlock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import xyz.aeolia.lib.utils.UUIDSerializer
import java.util.*

@Serializable
data class User(
  @Transient var online: Boolean = false,
  @Transient var pvpBlocks: MutableList<WorldBlock> = mutableListOf(),
  @SerialName("lastpvppayout") var lastPvpPayout: Long = 0,
  @SerialName("modmode") var modMode: Boolean = false,
  @Serializable(with = UUIDSerializer::class)
  var uuid: UUID? = null,
  var vanish: Boolean = false,
 )