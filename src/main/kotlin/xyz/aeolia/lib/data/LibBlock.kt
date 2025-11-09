package xyz.aeolia.lib.data

import org.bukkit.Bukkit
import org.bukkit.block.Block

class LibBlock(val world: String, val x: Int, val y: Int, val z: Int, val placer: User?) {

  @JvmOverloads
  constructor(block: Block, placer: User? = null) :
          this(block.world.name, block.x, block.y, block.z, placer)

  val block: Block
    get() = Bukkit.getWorld(world)!!.getBlockAt(x, y, z)

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is LibBlock) return false
    val that = other
    return x == that.x && y == that.y && z == that.z && world == that.world
  }

  override fun hashCode(): Int {
    var h = world.hashCode()
    h = 31 * h + x
    h = 31 * h + y
    h = 31 * h + z
    return h
  }
}