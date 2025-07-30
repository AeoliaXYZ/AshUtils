package xyz.aeolia.lib.data

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock
import org.mockbukkit.mockbukkit.plugin.PluginMock
import xyz.aeolia.lib.AeoliaLib
import xyz.aeolia.lib.sender.MessageSender

class ItemTest {
  lateinit var server: ServerMock
  lateinit var plugin: PluginMock

  @BeforeEach
  fun setUp() {
    server = MockBukkit.mock()
    plugin = MockBukkit.createMockPlugin()
    MessageSender.init(plugin)
  }

  @AfterEach
  fun tearDown() {
    MockBukkit.unmock()
  }

  @Test
  fun `test minimum item`() {
    val item = Item(
      material = "BLACK_WOOL"
    )
    val itemStack = item.loadStack()
    assertNotNull(itemStack)
    assertEquals(item.material, "BLACK_WOOL")
    assertNull(item.displayName)
    assertNull(item.lore)
    assertNull(item.enchantments)
  }

  @Test
  fun `test maximum item`() {
    val item = Item(
      displayName = "w",
      lore = listOf("hi"),
      material = "BLACK_WOOL",
      amount = 2,
      enchantments = mutableMapOf(
        "SHARPNESS" to 5
      )
    )
    val itemStack = item.loadStack()
    assertNotNull(itemStack)
    assertEquals(item.material, "BLACK_WOOL")
    assertNotNull(item.displayName)
    assertNotNull(item.lore)
    assertNotNull(item.enchantments)
  }

  @Test
  fun `test invalid item`() {
    val item = Item(
      material = "FOOBAR"
    )
    val itemStack = item.loadStack()
    assertNull(itemStack)
  }
}