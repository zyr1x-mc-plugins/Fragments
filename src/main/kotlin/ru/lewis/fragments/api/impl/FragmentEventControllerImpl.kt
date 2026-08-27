package ru.lewis.fragments.api.impl

import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.bukkit.Location
import ru.lewis.fragments.api.FragmentEventController
import ru.lewis.fragments.model.event.Event
import ru.lewis.fragments.model.event.impl.EventState

@Singleton
class FragmentEventControllerImpl @Inject constructor(
    private val event: Event,
    private val state: EventState,
) : FragmentEventController {

    override fun isRunning(): Boolean = state.isRunning

    override fun getLocation(): Location? = state.location

    override fun start() {
        event.start()
    }

    override fun stop() {
        event.stop()
    }
}
