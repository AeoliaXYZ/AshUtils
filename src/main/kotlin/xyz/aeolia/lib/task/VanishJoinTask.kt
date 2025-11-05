package xyz.aeolia.lib.task

import net.ess3.api.IUser
import org.bukkit.scheduler.BukkitRunnable
import xyz.aeolia.lib.user

class VanishJoinTask(val iUser: IUser) : BukkitRunnable() {
  override fun run() {
    iUser.base.user().vanish = iUser.isVanished
  }
}