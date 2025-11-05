package xyz.aeolia.lib.task

import net.ess3.api.events.VanishStatusChangeEvent

@Deprecated("Deprecated since 2.1.11: use VanishStatusChangeTask")
class VanishTask(event: VanishStatusChangeEvent) : VanishStatusChangeTask(event)