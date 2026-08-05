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
    private val plugin: Plugin,
    private val event: Event,
    private val configurationService: ConfigurationService
) : BukkitRunnable() {
    private val config get() = configurationService.eventConfigurationSection

    fun init() {
        val inTicks = config.repeat.inTicks
        this.runTaskTimer(plugin, inTicks, inTicks)
    }

    override fun run() {
        event.start()
    }
}