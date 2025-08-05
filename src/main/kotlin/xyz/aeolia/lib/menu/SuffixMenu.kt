package xyz.aeolia.lib.menu

import cymru.asheiou.inv.ClickableItem
import cymru.asheiou.inv.SmartInventory
import cymru.asheiou.inv.content.InventoryContents
import cymru.asheiou.inv.content.InventoryProvider
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.plugin.java.JavaPlugin
import xyz.aeolia.lib.manager.PermissionManager
import xyz.aeolia.lib.miniMessage
import xyz.aeolia.lib.sender.MessageSender.sendMessage


class SuffixMenu(private val plugin: JavaPlugin) : InventoryProvider {

  val inventory: SmartInventory = SmartInventory.builder()
    .id("suffixGui")
    .provider(this)
    .size(plugin.config.getInt("suffix.size"), 9)
    .title("Suffix Menu")
    .build()

  override fun init(player: Player, inventoryContents: InventoryContents) {
    val suffixList = plugin.config.getStringList("suffix.list")
    for (s in suffixList) {
      val formatted = formatSuffix(s, true)
      val formattedDeserialized: Component = formatted.miniMessage()
      val item: ItemStack
      val meta: ItemMeta

      if (player.hasPermission("group.$s")) {
        item = ItemStack(Material.EMERALD_BLOCK, 1)
        meta = item.itemMeta
        meta.displayName(formattedDeserialized)
        meta.lore(listOf(Component.text("Equipped!").color(NamedTextColor.AQUA)))
        item.setItemMeta(meta)
        inventoryContents.add(ClickableItem.of(item) { e: InventoryClickEvent ->
          e.isCancelled = true
          PermissionManager.groupUpdate(plugin, player.uniqueId, s, false)
          sendMessage(player, "Suffix unequipped.", true)
          player.closeInventory()
        })

      } else if (player.hasPermission("lib.suffix.$s")) {
        item = ItemStack(Material.DEEPSLATE, 1)
        meta = item.itemMeta
        meta.displayName(formattedDeserialized)
        meta.lore(listOf(Component.text("Equip").color(NamedTextColor.GREEN)))
        item.setItemMeta(meta)
        inventoryContents.add(ClickableItem.of(item) {
          it.isCancelled = true

          for (s1 in suffixList) {
            if (player.hasPermission("group.$s1"))
              PermissionManager.groupUpdate(plugin, player.uniqueId, s1, false)
          }
          PermissionManager.groupUpdate(plugin, player.uniqueId, s, true)

          sendMessage(
            player, "Suffix changed to " + formatSuffix(s, true) + ".",
            true
          )
          player.closeInventory()
        })

      }
    }

    val playerHead = ItemStack(Material.PLAYER_HEAD, 1)
    val playerHeadMeta = playerHead.itemMeta as SkullMeta
    playerHeadMeta.owningPlayer = player
    playerHeadMeta.displayName(
      Component.text("Want more suffixes?")
        .color(NamedTextColor.GOLD)
    )
    playerHeadMeta.lore(
      listOf(Component.text("Click here to find out more!").color(NamedTextColor.WHITE))
    )
    playerHead.setItemMeta(playerHeadMeta)
    inventoryContents.add(ClickableItem.of(playerHead) {
      it.isCancelled = true
      sendMessage(
        player, "<aqua><click:open_url:'" + plugin.config
          .getString("suffix.url") + "'>Click here</click></aqua> to learn more about suffixes!", true
      )
      player.closeInventory()
    })
  }

  override fun update(player: Player, inventoryContents: InventoryContents) {
  }

  companion object {
    @JvmStatic
    fun formatSuffix(s: String, miniMessage: Boolean): String {
      val formatted = "the " + s
        .replace('_', ' ')
        .lowercase()
        .split(' ')
        .joinToString(" ") { word ->
          word.replaceFirstChar { it.uppercaseChar() }
        }
        .split('-')
        .joinToString("-") { segment ->
          segment.replaceFirstChar { it.uppercaseChar() }
        }

      if (miniMessage) {
        return "<gray><italic>$formatted</italic></gray>"
      }
      return "&7&o$formatted"
    }
  }
}