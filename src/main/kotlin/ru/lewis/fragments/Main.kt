package ru.lewis.fragments

import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.bukkit.plugin.Plugin
import ru.lewis.fragments.listener.NpcListener
import ru.lewis.fragments.model.PlaceholderExpansion
import ru.lewis.fragments.model.event.Event
import ru.lewis.fragments.service.CommandService
import ru.lewis.fragments.service.DatabaseService
import ru.lewis.fragments.service.RedisService
import ru.lewis.fragments.service.ConfigurationService
import ru.lewis.fragments.service.FragmentsService
import xyz.xenondevs.invui.InvUI

@Singleton
class Main @Inject constructor(
    private val plugin: Plugin,
    private val configurationService: ConfigurationService,
    private val redisService: RedisService,
    private val commandService: CommandService,
    private val databaseService: DatabaseService,
    private val npcListener: NpcListener,
    private val fragmentsService: FragmentsService,
    private val event: Event,
    private val placeholderExpansion: PlaceholderExpansion
) {
    fun enable() {
        InvUI.getInstance().setPlugin(plugin);
        configurationService.run()
        databaseService.init()
        redisService.getClient()
        fragmentsService.loadCache()
        commandService.register()
        placeholderExpansion.register()

        registerListeners()
    }

    fun disable() {
        commandService.unregister()
        event.stop()
        placeholderExpansion.unregister()
    }

    private fun registerListeners() {
        plugin.server.pluginManager.registerEvents(npcListener, plugin)
    }
}