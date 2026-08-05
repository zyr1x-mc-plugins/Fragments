package ru.lewis.fragments

import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.bukkit.plugin.Plugin
import ru.lewis.fragments.api.FragmentsApi
import ru.lewis.fragments.listener.NpcListener
import ru.lewis.fragments.listener.ServerLoadedListener
import ru.lewis.fragments.model.FragmentsEconomyImpl
import ru.lewis.fragments.model.FragmentsUserEntity
import ru.lewis.fragments.model.PlaceholderExpansion
import ru.lewis.fragments.model.event.Event
import ru.lewis.fragments.service.CommandService
import ru.lewis.fragments.service.ConfigurationService
import ru.lewis.fragments.service.FragmentsService
import ru.lewis.fragments.task.EventTask
import ru.lewis.point.api.PointAPI
import xyz.xenondevs.invui.InvUI

@Singleton
class Main @Inject constructor(
    private val plugin: Plugin,
    private val configurationService: ConfigurationService,
    private val commandService: CommandService,
    private val npcListener: NpcListener,
    private val serverLoadedListener: ServerLoadedListener,
    private val pointAPI: PointAPI,
    private val event: Event,
    private val placeholderExpansion: PlaceholderExpansion,
    private val fragmentsEconomyImpl: FragmentsEconomyImpl,
    private val eventTask: EventTask
) {
    fun enable() {
        InvUI.getInstance().setPlugin(plugin);
        configurationService.run()
        commandService.register()
        placeholderExpansion.register()
        eventTask.init()

        registerEntities()
        registerListeners()
        registerApi()
    }

    fun disable() {
        commandService.unregister()
        event.stop()
        placeholderExpansion.unregister()
    }

    private fun registerListeners() {
        val pluginManager = plugin.server.pluginManager

        pluginManager.registerEvents(npcListener, plugin)
        pluginManager.registerEvents(serverLoadedListener, plugin)
    }

    private fun registerApi() {
        FragmentsApi.init(fragmentsEconomyImpl)
    }

    private fun registerEntities() {
        pointAPI.databaseService.registerEntity(FragmentsUserEntity::class.java)
    }
}