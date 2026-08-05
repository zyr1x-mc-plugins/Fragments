package ru.lewis.fragments.listener

import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerLoadEvent
import ru.lewis.fragments.service.FragmentsService

@Singleton
class ServerLoadedListener @Inject constructor(
    private val fragmentsService: FragmentsService
) : Listener {

    @EventHandler
    fun onServerLoadedEvent(event: ServerLoadEvent) {
        fragmentsService.loadCache()
    }
}