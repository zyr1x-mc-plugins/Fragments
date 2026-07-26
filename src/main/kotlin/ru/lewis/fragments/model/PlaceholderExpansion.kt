package ru.lewis.fragments.model

import jakarta.inject.Inject
import jakarta.inject.Singleton
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.entity.Player
import ru.lewis.fragments.model.event.impl.EventState
import ru.lewis.fragments.service.ConfigurationService
import ru.lewis.fragments.service.FragmentsService

@Singleton
class PlaceholderExpansion @Inject constructor(
    private val fragmentsService: FragmentsService,
    private val configurationService: ConfigurationService,
    private val eventState: EventState
) : PlaceholderExpansion() {
    override fun getIdentifier(): String = "fragments"

    override fun getAuthor(): String = "Lewis"

    override fun getVersion(): String = "1.0.0"

    override fun onPlaceholderRequest(player: Player, params: String): String {
        if (params.contentEquals("balance")) {
            return fragmentsService.getCount(player.uniqueId).toString()
        } else if (params.contentEquals("location")) {
            if (eventState.isRunning) {
                val legacyText = LegacyComponentSerializer.legacySection().serialize(
                    configurationService.messageConfigurationSection.placeholder.eventStart.resolve(
                        Placeholder.unparsed("x", eventState.location!!.blockX.toString()),
                        Placeholder.unparsed("z", eventState.location!!.blockZ.toString())
                    ).asComponent()
                )

                return legacyText
            } else {
                val legacyText = LegacyComponentSerializer.legacySection().serialize(
                    configurationService.messageConfigurationSection.placeholder.eventStop.asComponent()
                )

                return legacyText
            }
        }

        return "not null"
    }
}