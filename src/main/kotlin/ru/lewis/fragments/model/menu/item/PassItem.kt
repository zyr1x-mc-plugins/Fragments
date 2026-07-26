package ru.lewis.fragments.model.menu.item

import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import ru.lewis.fragments.config.MessageConfiguration
import ru.lewis.fragments.config.type.MenuConfig
import ru.lewis.fragments.model.ItemStrings
import ru.lewis.fragments.model.PurchaseManager
import ru.lewis.fragments.service.FragmentsService
import ru.lewis.fragments.service.ItemService
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.ItemWrapper
import xyz.xenondevs.invui.item.impl.AbstractItem

class PassItem(
    private val menuTemplate: MenuConfig,
    private val itemService: ItemService,
    private val fragmentsService: FragmentsService,
    private val purchaseManager: PurchaseManager,
    private val messageConfiguration: MessageConfiguration
) : AbstractItem() {
    override fun getItemProvider(): ItemProvider {
        return ItemWrapper(menuTemplate.templates['T']!!.toItem())
    }

    override fun handleClick(
        clickType: ClickType,
        player: Player,
        event: InventoryClickEvent
    ) {
        if (!purchaseManager.purchase) {
            player.closeInventory()
            player.sendMessage(messageConfiguration.event.playersInArea)
            return
        }
        if (!clickType.isLeftClick) return
        val count = itemService.count(player, ItemStrings.map)
        itemService.delete(player, ItemStrings.map, count)
        fragmentsService.addCount(player.uniqueId, count)
    }
}