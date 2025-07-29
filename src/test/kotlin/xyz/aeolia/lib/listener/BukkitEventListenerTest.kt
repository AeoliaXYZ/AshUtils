package xyz.aeolia.lib.listener

import io.papermc.paper.chat.ChatRenderer
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.chat.SignedMessage
import net.kyori.adventure.text.Component
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock
import org.mockbukkit.mockbukkit.plugin.PluginMock
import org.mockito.Mockito.mock
import xyz.aeolia.lib.manager.StatusManager
import xyz.aeolia.lib.sender.MessageSender

class BukkitEventListenerTest {
  lateinit var server: ServerMock
  lateinit var plugin: PluginMock

  @BeforeEach
  fun setUp() {
    server = MockBukkit.mock()
    plugin = MockBukkit.createMockPlugin()

    MessageSender.init(plugin)
    StatusManager.setStatus("lockchat", false)
  }

  @AfterEach
  fun tearDown() {
    MockBukkit.unmock()
  }

  @Test
  fun testChatCapsBlock() {
    try {
      val chatEvent = createAsyncChatEvent(":BLARG")
      BukkitEventListener(plugin).onChat(chatEvent)
      Assertions.assertTrue(chatEvent.isCancelled)
    } catch (t: Throwable) {
      t.printStackTrace()
    }
  }

  @Test
  fun testLockChat() {
    try {
      StatusManager.setStatus("lockchat", true)
      val chatEvent = createAsyncChatEvent("hello")
      BukkitEventListener(plugin).onChat(chatEvent)
      Assertions.assertTrue(chatEvent.isCancelled)
    } catch (t: Throwable) {
      t.printStackTrace()
    }
  }

  @Test
  fun testNormalChat() {
    try {
      StatusManager.setStatus("lockchat", false)
      val chatEvent = createAsyncChatEvent("hello")
      BukkitEventListener(plugin).onChat(chatEvent)
      Assertions.assertFalse(chatEvent.isCancelled)
    } catch (t: Throwable) {
      t.printStackTrace()
    }
  }

  fun createAsyncChatEvent(message: String): AsyncChatEvent {
    val playerMock = server.addPlayer()
    val testComponent = Component.text(message)
    val signedMessage = SignedMessage.system(message, testComponent)
    val audiences = setOf(mock(Audience::class.java))
    val renderer = mock(ChatRenderer::class.java)
    return AsyncChatEvent(
      true,
      playerMock,
      audiences,
      renderer,
      testComponent,
      testComponent,
      signedMessage
    )
  }
}