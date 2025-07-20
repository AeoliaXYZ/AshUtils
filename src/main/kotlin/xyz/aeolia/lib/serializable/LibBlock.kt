package xyz.aeolia.lib.serializable

import org.bukkit.Bukkit
import org.bukkit.block.Block

class LibBlock(val world: String, val x: Int, val y: Int, val z: Int, val placer: User?) {

  @JvmOverloads
  constructor(block: Block, placer: User? = null) : this(block.world.name, block.x, block.y, block.z, placer)

  val block: Block
    get() = Bukkit.getWorld(world)!!.getBlockAt(x, y, z)
}