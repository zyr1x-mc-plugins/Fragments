package ru.lewis.fragments.model.event.impl

import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.bukkit.Location
import ru.lewis.fragments.model.event.Event
import ru.lewis.fragments.model.event.impl.handle.EventStartHandle
import ru.lewis.fragments.model.event.impl.handle.EventStopHandle
import ru.lewis.fragments.model.event.impl.handle.EventTickHandle

@Singleton
class EventImpl @Inject constructor(
    private val eventStartHandle: EventStartHandle,
    private val eventStopHandle: EventStopHandle,
    private val eventTickHandle: EventTickHandle
) : Event {
    override fun start() = eventStartHandle.start()
    override fun onTick(location: Location) = eventTickHandle.onTick(location)
    override fun stop() = eventStopHandle.stop()
}