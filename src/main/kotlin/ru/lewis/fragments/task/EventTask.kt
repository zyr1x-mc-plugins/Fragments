package ru.lewis.fragments.task

import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import ru.lewis.fragments.extensions.inTicks
import ru.lewis.fragments.model.event.Event
import ru.lewis.fragments.service.ConfigurationService

@Singleton
class EventTask @Inject constructor(
    plugin: Plugin,
    private val configurationService: ConfigurationService,
    private val event: Event,
): BukkitRunnable() {
    private val eventSection get() = configurationService.eventConfigurationSection

    init {
        val ticks = eventSection.repeat.inTicks
        this.runTaskTimer(plugin, ticks, ticks)
    }

    override fun run() {
        event.start()
    }
}