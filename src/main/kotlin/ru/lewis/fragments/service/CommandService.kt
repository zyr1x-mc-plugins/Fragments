package ru.lewis.fragments.service

import dev.rollczi.litecommands.LiteCommands
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin
import ru.lewis.fragments.commands.FragmentCommand

@Singleton
class CommandService @Inject constructor(
    private val plugin: Plugin,
    private val fragmentCommand: FragmentCommand
) {
    private lateinit var commands: LiteCommands<CommandSender>

    fun register() {
        commands = LiteBukkitFactory.builder(plugin.name, plugin)
            .commands(fragmentCommand)
            .build()
    }

    fun unregister() {
        commands.unregister()
    }
}