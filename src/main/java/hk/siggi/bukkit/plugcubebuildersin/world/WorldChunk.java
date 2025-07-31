package hk.siggi.bukkit.plugcubebuildersin.world;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

public final class WorldChunk {

  public final String world;
  public final int x, z;

  public WorldChunk(WorldBlock block) {
    this(block.world, block.x >> 4, block.z >> 4);
  }

  public WorldChunk(Chunk chunk) {
    this(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
  }

  public WorldChunk(String world, int x, int z) {
    if (world == null) {
      throw new NullPointerException();
    }
    this.world = world;
    this.x = x;
    this.z = z;
  }

  public Chunk getBukkitChunk() {
    return Bukkit.getWorld(world).getChunkAt(x, z);
  }

  public boolean isChunkLoaded() {
    try {
      World w = Bukkit.getWorld(world);
      if (w == null) {
        return false;
      }
      return w.isChunkLoaded(x, z);
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof WorldChunk) {
      return equals((WorldChunk) other);
    }
    return false;
  }

  public boolean equals(WorldChunk other) {
    if (other == null) {
      return false;
    }
    return world.equals(other.world) && x == other.x && z == other.z;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 37 * hash + this.world.hashCode();
    hash = 37 * hash + this.x;
    hash = 37 * hash + this.z;
    return hash;
  }
}