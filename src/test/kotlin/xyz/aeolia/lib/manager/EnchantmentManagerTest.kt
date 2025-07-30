package xyz.aeolia.lib.manager

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock
import org.mockbukkit.mockbukkit.entity.PlayerMock
import org.mockbukkit.mockbukkit.plugin.PluginMock
import xyz.aeolia.lib.command.user.EnchantTabExecutor
import xyz.aeolia.lib.data.EnchantResult

class EnchantmentManagerTest {
  lateinit var server: ServerMock
  lateinit var plugin: PluginMock
  lateinit var player: PlayerMock
  lateinit var command: EnchantTabExecutor

  @BeforeEach
  fun setUp() {
    server = MockBukkit.mock()
    plugin = MockBukkit.createMockPlugin()
    player = server.addPlayer()
    EnchantmentManager.init(plugin)
  }

  @AfterEach
  fun tearDown() {
    MockBukkit.unmock()
  }

  @Test
  fun `test safe enchant`() {
    val itemStack = ItemStack(Material.STONE_SWORD, 1)
    player.setItemInHand(itemStack)
    val enchant = EnchantmentManager.nameToEnchant("sharpness") ?: fail("Sharpness is invalid")
    val enchantResult = EnchantmentManager.addSafeEnchant(enchant, 5, itemStack)
    assertEquals(enchantResult, EnchantResult.SUCCESS)
    assertNotNull(player.itemInHand.enchantments)
  }
}