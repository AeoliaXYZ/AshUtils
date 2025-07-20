package xyz.aeolia.lib.listener;

import hk.siggi.bukkit.plugcubebuildersin.world.WorldBlock;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.aeolia.lib.sender.MessageSender;
import xyz.aeolia.lib.sender.WebhookSender;
import xyz.aeolia.lib.manager.UserManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Pattern;

/*
Based on
https://github.com/CubeBuilders/PlugCubeBuildersIn/blob/main/src/main/java/hk/siggi/bukkit/plugcubebuildersin/module/MineWatchModuleImpl.java
by Sigurður Jón/WesternIcelander, licensed to me as MIT
Changes made:
- Reduced dependence on PlugCubeBuildersIn classes and functions
- New implementation for sendMine to work on Bukkit and not BungeeCord (and to POST a webhook)
 */

public class MineListener implements Listener {
  private static final BlockFace[] faces = new BlockFace[]{BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST};
  private final JavaPlugin plugin;
  private final Map<Material, String> materials = new HashMap<>();
  private final Map<WorldBlock, Long> blocksFound = new HashMap<>();

  public MineListener(JavaPlugin plugin) {
    this.plugin = plugin;
    materials.put(Material.IRON_ORE, "iron");
    materials.put(Material.GOLD_ORE, "gold");
    materials.put(Material.COAL_ORE, "coal");
    materials.put(Material.LAPIS_ORE, "lapis");
    materials.put(Material.DIAMOND_ORE, "diamond");
    materials.put(Material.REDSTONE_ORE, "redstone");
    materials.put(Material.EMERALD_ORE, "emerald");
    try {
      materials.put(Material.NETHER_QUARTZ_ORE, "quartz");
    } catch (Throwable t) {
      materials.put(Material.getMaterial("QUARTZ_ORE"), "quartz");
      materials.put(Material.getMaterial("GLOWING_REDSTONE_ORE"), "redstone");
    }
    try {
      // 1.16
      materials.put(Material.NETHER_GOLD_ORE, "nethergold");
      materials.put(Material.ANCIENT_DEBRIS, "ancientdebris");
    } catch (Throwable t) {
    }
    try {
      // 1.17 & 1.18
      materials.put(Material.DEEPSLATE_IRON_ORE, "iron");
      materials.put(Material.DEEPSLATE_GOLD_ORE, "gold");
      materials.put(Material.DEEPSLATE_COAL_ORE, "coal");
      materials.put(Material.DEEPSLATE_LAPIS_ORE, "lapis");
      materials.put(Material.DEEPSLATE_DIAMOND_ORE, "diamond");
      materials.put(Material.DEEPSLATE_REDSTONE_ORE, "redstone");
      materials.put(Material.DEEPSLATE_EMERALD_ORE, "emerald");

      materials.put(Material.COPPER_ORE, "copper");
      materials.put(Material.DEEPSLATE_COPPER_ORE, "copper");
    } catch (Throwable t) {
    }
  }

  public void init() {
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  public void tick() {
    long now = System.currentTimeMillis();
    for (Iterator<WorldBlock> iterator = blocksFound.keySet().iterator(); iterator.hasNext(); ) {
      WorldBlock wb = iterator.next();
      Long timeL = blocksFound.get(wb);
      if (timeL == null) {
        iterator.remove();
        continue;
      }
      long time = timeL;
      if (time + 1200000L <= now) { // 20 minutes
        iterator.remove();
      }
    }
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void blockBreakEvent(BlockBreakEvent event) {
    Block block = event.getBlock();
    Material material = block.getType();
    if (!materials.containsKey(material)) {
      return;
    }
    WorldBlock wb = new WorldBlock(block);
    if (blocksFound.containsKey(wb)) {
      return;
    }
    Set<Block> findSimilarAdjacentBlocks = findSimilarAdjacentBlocks(block);
    long now = System.currentTimeMillis();
    for (Block b : findSimilarAdjacentBlocks) {
      blocksFound.put(new WorldBlock(b), now);
    }
    Player p = event.getPlayer();
    String materialName = materials.get(material);
    int count = findSimilarAdjacentBlocks.size();
    int lightLevel = (int) block.getLightLevel();
    for (BlockFace f : faces) {
      try {
        lightLevel = Math.max(lightLevel, block.getRelative(f).getLightLevel());
      } catch (Exception e) {
      }
    }
    sendMine(p, block, materialName, count, lightLevel);
  }

  private Set<Block> findSimilarAdjacentBlocks(Block block) {
    Material material = block.getType();
    World w = block.getWorld();
    Set<Block> blocksToRecurseFrom = new HashSet<>();
    Set<Block> checkedBlocks = new HashSet<>();
    Set<Block> matchedBlocks = new HashSet<>();
    blocksToRecurseFrom.add(block);
    checkedBlocks.add(block);
    matchedBlocks.add(block);
    Set<Block> newBlocks = new HashSet<>();
    while (true) {
      for (Block b : blocksToRecurseFrom) {
        int originalX = b.getX();
        int originalY = b.getY();
        int originalZ = b.getZ();
        for (int y = Math.max(w.getMinHeight(), originalY - 1); y <= Math.min(w.getMaxHeight() - 1, originalY + 1); y++) {
          for (int z = originalZ - 1; z <= originalZ + 1; z++) {
            for (int x = originalX - 1; x <= originalX + 1; x++) {
              if (x == originalX && y == originalY && z == originalZ) {
                continue;
              }
              Block bl = w.getBlockAt(x, y, z);
              if (checkedBlocks.contains(bl)) {
                continue;
              }
              checkedBlocks.add(bl);
              Material blockType = bl.getType();
              if (isSameMaterial(material, blockType)) {
                matchedBlocks.add(bl);
                newBlocks.add(bl);
                if (matchedBlocks.size() >= 100000) {
                  return matchedBlocks;
                }
              }
            }
          }
        }
      }
      if (newBlocks.isEmpty()) {
        break;
      }
      blocksToRecurseFrom.clear();
      blocksToRecurseFrom.addAll(newBlocks);
      newBlocks.clear();
    }
    return matchedBlocks;
  }

  private boolean isSameMaterial(Material material1, Material material2) {
    if (material1 == material2)
      return true;
    String material1Name = materials.get(material1);
    String material2Name = materials.get(material2);
    return Objects.equals(material1Name, material2Name);
  }

  private void sendMine(Player p, Block block, String ore, int count, int lightLevel) {
    String message = "<aqua>" + p.getName() + "</aqua><yellow> found </yellow><aqua>" + count + "x " + ore +
            "</aqua><yellow> at </yellow><aqua>" + block.getX() + " "
            + block.getY() + " " + block.getZ() + " in " + block.getWorld().getName()
            + "</aqua><yellow> (light level: </yellow><aqua>" + lightLevel + "</aqua><yellow>)!";
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (player.hasPermission("lib.alert"))
        if (UserManager.getUser(player).getModMode())
          MessageSender.sendMessage(player, "<gold><bold>!! <reset>" + message, false);
    }
    URI uri;
    try {
      uri = new URI(Objects.requireNonNull(plugin.getConfig().getString("discord.mine-webhook")));
    } catch (URISyntaxException e) {
      plugin.getLogger().warning("mine-webhook URI could not be parsed.");
      return;
    }

    Pattern pattern = Pattern.compile("<(?:[a-zA-Z0-9_-]+:|)[^>]+>|\\{[^}]+}");
    String stripped = pattern.matcher(message).replaceAll("");
    WebhookSender.postWebhook(uri, stripped);
  }

}