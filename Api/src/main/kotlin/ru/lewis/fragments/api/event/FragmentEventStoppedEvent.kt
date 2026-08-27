package ru.lewis.fragments.api.event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Called after the Fragments event has stopped.
 *
 * This event is fired on the main server thread after the schematic, region and NPC have
 * been cleaned up and the tick loop has stopped. It is a notification event and cannot be
 * cancelled.
 */
class FragmentEventStoppedEvent : Event() {

    override fun getHandlers(): HandlerList = HANDLERS

    companion object {
        private val HANDLERS = HandlerList()
        @JvmStatic
        fun getHandlerList(): HandlerList = HANDLERS
    }
}
