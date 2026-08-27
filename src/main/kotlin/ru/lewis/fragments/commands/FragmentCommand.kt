package ru.lewis.fragments.commands

import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.permission.Permission
import jakarta.inject.Inject
import jakarta.inject.Singleton
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.entity.Player
import ru.lewis.fragments.model.ItemStrings
import ru.lewis.fragments.model.event.Event
import ru.lewis.fragments.service.FragmentsService
import ru.lewis.fragments.service.ConfigurationService
import ru.lewis.fragments.service.ItemService

@Command(name = "fragment", aliases = ["f"])
@Permission("fragment.use")
@Singleton
class FragmentCommand @Inject constructor(
    private val fragmentsService: FragmentsService,
    private val configurationService: ConfigurationService,
    private val itemService: ItemService,
    private val event: Event
){
    @Execute(name = "reload")
    fun reload(){
        configurationService.run()
    }

    @Execute(name = "event start")
    fun startEvent() {
        event.start()
    }

    @Execute(name = "event stop")
    fun stopEvent() {
        event.stop()
    }

    @Execute(name = "get")
    fun getItem(@Context player: Player, @Arg count: Int) {
        val itemConfig = configurationService.configurationSection.fragmentItem
        val item = itemConfig.toItem()
        item.amount = count

        itemService.applyStrings(
            item,
            ItemStrings.map
        )

        player.inventory.addItem(item)
    }

    @Execute(name = "give")
    fun giveItem(@Arg player: Player, @Arg count: Int) {
        val itemConfig = configurationService.configurationSection.fragmentItem
        val item = itemConfig.toItem()
        item.amount = count

        itemService.applyStrings(
            item,
            ItemStrings.map
        )

        player.inventory.addItem(item)
    }

    @Execute(name = "balance")
    fun checkBalance(@Context player: Player) {
        val uniqueId = player.uniqueId
        val count = fragmentsService.getCount(uniqueId)

        val message = configurationService.messageConfigurationSection.checkBalance
            .resolve(Formatter.number("balance", count))

        player.sendMessage(message)
    }

    @Execute(name = "balance")
    fun checkBalance(@Context sender: Player, @Arg player: Player) {
        val uniqueId = player.uniqueId
        val count = fragmentsService.getCount(uniqueId)

        val message = configurationService.messageConfigurationSection.checkBalance
            .resolve(Formatter.number("balance", count))

        sender.sendMessage(message)
    }

    @Execute(name = "set")
    fun set(@Context sender: Player, @Arg player: Player, @Arg count: Int) {
        val uniqueId = player.uniqueId
        val displayName = player.name()

        fragmentsService.setCount(uniqueId, count)

        val message = configurationService.messageConfigurationSection.setBalance
            .resolve(
                Formatter.number("balance", count),
                Placeholder.component("player", displayName)
            )

        sender.sendMessage(message)
    }
}