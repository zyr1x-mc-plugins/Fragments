package ru.lewis.fragments.api.event

import org.bukkit.Location
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Called after the Fragments event has fully started.
 *
 * This event is fired on the main server thread once the schematic has been placed, the
 * NPC spawned, the region created and the tick loop is running. It is a notification
 * event and cannot be cancelled.
 *
 * @property location the location where the event schematic was placed
 */
class FragmentEventStartedEvent(
    val location: Location,
) : Event() {

    override fun getHandlers(): HandlerList = HANDLERS

    companion object {
        private val HANDLERS = HandlerList()
        @JvmStatic
        fun getHandlerList(): HandlerList = HANDLERS
    }
}
