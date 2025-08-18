package xyz.aeolia.lib.task

import net.ess3.api.events.VanishStatusChangeEvent
import org.bukkit.scheduler.BukkitRunnable
import xyz.aeolia.lib.user

class VanishTask(val event: VanishStatusChangeEvent) : BukkitRunnable() {
  override fun run() {
    event.affected.base.user().vanish = event.value;
  }
}