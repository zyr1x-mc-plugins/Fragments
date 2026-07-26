package ru.lewis.fragments.listener

import de.oliver.fancynpcs.api.events.NpcInteractEvent
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import ru.lewis.fragments.model.menu.EventMenuFragments
import ru.lewis.fragments.service.FancyNpcService

@Singleton
class NpcListener @Inject constructor(
    private val eventMenuFragments: EventMenuFragments
) : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    fun onInteract(event: NpcInteractEvent) {
        if (event.npc.data.name != FancyNpcService.NPC_DATA_KEY) return
        val player = event.player
        eventMenuFragments.open(player)
    }
}