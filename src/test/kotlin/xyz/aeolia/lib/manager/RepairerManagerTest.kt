package xyz.aeolia.lib.manager

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock
import org.mockbukkit.mockbukkit.entity.PlayerMock
import org.mockbukkit.mockbukkit.plugin.PluginMock
import kotlin.test.Test

class RepairerManagerTest {
  lateinit var server: ServerMock
  lateinit var plugin: PluginMock
  lateinit var player: PlayerMock
  lateinit var controlItem: ItemStack

  @BeforeEach
  fun setUp() {
    server = MockBukkit.mock()
    plugin = MockBukkit.createMockPlugin()
    player = server.addPlayer()
    controlItem = ItemStack(Material.STONE_SHOVEL)
    (controlItem.itemMeta as Damageable).apply { damage = 10 }
    player.setItemInHand(controlItem)
  }

  @AfterEach
  fun tearDown() {
    MockBukkit.unmock()
  }

  @Test
  fun init() {
    val rm = RepairerManager(plugin,
      player,
      listOf(player.itemInHand),
      true,
      1.0)
    assertFalse(rm.costToRepair == 0.0)
    assertFalse(rm.repairers.isEmpty())
  }
}