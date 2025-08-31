package xyz.aeolia.lib.manager

/* import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock
import org.mockbukkit.mockbukkit.entity.PlayerMock
import org.mockbukkit.mockbukkit.inventory.ItemStackMock
import org.mockbukkit.mockbukkit.plugin.PluginMock
import xyz.aeolia.lib.data.EnchantResult
 */

class EnchantmentManagerTest {
  /*
  lateinit var server: ServerMock
  lateinit var plugin: PluginMock
  lateinit var player: PlayerMock
  lateinit var controlStack: ItemStackMock
  lateinit var itemStack: ItemStackMock

  @BeforeEach
  fun setUp() {
    server = MockBukkit.mock()
    plugin = MockBukkit.createMockPlugin()
    player = server.addPlayer()
    EnchantmentManager.init(plugin)
    controlStack = ItemStackMock(Material.STONE_SWORD, 1)
    itemStack = ItemStackMock(Material.STONE_SWORD, 1)
  }

  @AfterEach
  fun tearDown() {
    MockBukkit.unmock()
  }

  @Test
  fun `test safe enchant`() {
    player.setItemInHand(itemStack)
    val enchant = EnchantmentManager.nameToEnchant("sharpness") ?: fail("Sharpness is invalid")
    val enchantResult = EnchantmentManager.addSafeEnchant(enchant, 5, player.itemInHand)
    assertEquals(enchantResult, EnchantResult.SUCCESS)
    assertNotEquals(player.itemInHand.enchantments, controlStack.enchantments)
  }

  @Test
  fun `test illegal enchant`() {
    player.setItemInHand(itemStack)
    val enchant = EnchantmentManager.nameToEnchant("aqua_affinity") ?: fail("aqua affinity is invalid")
    val enchantResult = EnchantmentManager.addSafeEnchant(enchant, 3, itemStack)
    assertEquals(enchantResult, EnchantResult.INCOMPATIBLE_ENCHANTMENT)
    assertEquals(player.itemInHand.enchantments, controlStack.enchantments)
  }

  @Test
  fun `test conflicting enchant`() {
    itemStack.addEnchantment(Enchantment.SHARPNESS, 5)
    val controlStack = ItemStackMock(controlStack)
    val enchantResult = EnchantmentManager.addSafeEnchant(Enchantment.SMITE, 4, itemStack)
    assertEquals(enchantResult, EnchantResult.CONFLICTING_ENCHANTMENTS)
    assertEquals(itemStack.enchantments, controlStack.enchantments)
  }*/
}