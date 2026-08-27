package ru.lewis.fragments.model.event.impl.handle

import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.bukkit.plugin.Plugin
import ru.lewis.fragments.api.event.FragmentEventStoppedEvent
import ru.lewis.fragments.extensions.broadCast
import ru.lewis.fragments.model.event.impl.EventState
import ru.lewis.fragments.service.ConfigurationService
import ru.lewis.fragments.service.FancyNpcService
import ru.lewis.fragments.service.WorldEditHelper
import ru.lewis.fragments.service.WorldGuardHelper

@Singleton
class EventStopHandle @Inject constructor(
    private val plugin: Plugin,
    private val worldEditHelper: WorldEditHelper,
    private val configurationService: ConfigurationService,
    private val npcService: FancyNpcService,
    private val worldGuardHelper: WorldGuardHelper,
    private val eventTickHandle: EventTickHandle,
    private val state: EventState
) {
    private val message get() = configurationService.messageConfigurationSection

    fun stop() {
        plugin.logger.info("[Event] stop() вызван, isRunning=${state.isRunning}")
        if (!state.isRunning) {
            plugin.logger.info("[Event] Событие не запущено, выхожу")
            return
        }

        try {
            state.editSession?.let {
                plugin.logger.info("[Event] Отменяю editSession")
                worldEditHelper.undo(it)
            }

            val location = state.location
            val region = state.region
            if (location != null && region != null) {
                plugin.logger.info("[Event] Удаляю регион $region")
                worldGuardHelper.deleteRegion(region, location.world)
            }

            plugin.logger.info("[Event] Удаляю NPC")
            npcService.delete()

            plugin.logger.info("[Event] Очищаю tick handle")
            eventTickHandle.clear()

            broadCast(message.event.stop.message)

            plugin.logger.info("[Event] state.clear()")
            state.clear()

            plugin.server.pluginManager.callEvent(FragmentEventStoppedEvent())
        } catch (exception: Exception) {
            plugin.logger.log(java.util.logging.Level.SEVERE, "[Event] Ошибка при остановке события", exception)
        }
    }
}