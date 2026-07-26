package ru.lewis.fragments.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import ru.lewis.fragments.config.type.MiniMessageComponent
import ru.lewis.fragments.config.type.SoundConfiguration
import ru.lewis.fragments.config.type.TitleConfiguration
import ru.lewis.fragments.extensions.asMiniMessageComponent

@ConfigSerializable
data class MessageConfiguration(
    val checkBalance: MiniMessageComponent = "<red>Ваш баланас: <balance>".asMiniMessageComponent(),
    val setBalance: MiniMessageComponent = "<green>Вы успешно установили новый баланс <balance> для <player>".asMiniMessageComponent(),

    val event: Event = Event(),
    val placeholder: Placeholder = Placeholder()
) {
    @ConfigSerializable
    data class Placeholder(
        val eventStart: MiniMessageComponent = "Ивент запущен на координатах <x> <z>".asMiniMessageComponent(),
        val eventStop: MiniMessageComponent = "Ивент не запущен".asMiniMessageComponent()
    )

    @ConfigSerializable
    data class Event(
        val start: Start = Start(),
        val stop: Stop = Stop(),
        val menu: Menu = Menu(),
        val playersInArea: MiniMessageComponent = "Дружище, я не могу принять фрагменты, ты должен быть один!".asMiniMessageComponent(),
    ) {
        @ConfigSerializable
        data class Start(
            val message: MiniMessageComponent = "<green>Началось событие Захват фрагментов! Координаты: x: <x> z: <z>".asMiniMessageComponent(),
            val title: TitleConfiguration = TitleConfiguration("<red>Захват фрагментов".asMiniMessageComponent(), "<gray>Начался!!!".asMiniMessageComponent()),
            val sound: SoundConfiguration = SoundConfiguration()
        )

        @ConfigSerializable
        data class Stop(
            val message: MiniMessageComponent = "<red>Событие Захват фрагментов закончен.".asMiniMessageComponent(),
        )

        @ConfigSerializable
        data class Menu(
            val needFragments: MiniMessageComponent = "<red>У вас недостаточно фрагментов".asMiniMessageComponent(),
            val successfullyBuy: MiniMessageComponent = "<green>Вы успешно приобрели <item-name>".asMiniMessageComponent()
        )
    }
}