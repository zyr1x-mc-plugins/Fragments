package ru.lewis.fragments.model.menu.item

import net.kyori.adventure.text.minimessage.tag.resolver.Formatter
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import ru.lewis.fragments.config.MessageConfiguration
import ru.lewis.fragments.config.ShopConfiguration
import ru.lewis.fragments.model.ItemStrings
import ru.lewis.fragments.model.PurchaseManager
import ru.lewis.fragments.service.ItemService
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.ItemWrapper
import xyz.xenondevs.invui.item.impl.AbstractItem

class PurchasableItem(
    val player: Player,
    val itemSection: ShopConfiguration.ItemSection,
    val itemService: ItemService,
    val shopConfiguration: ShopConfiguration,
    val messageConfiguration: MessageConfiguration,
    val purchaseManager: PurchaseManager
) : AbstractItem() {

    override fun getItemProvider(): ItemProvider {
        return ItemWrapper(resolve())
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

        if (itemSection.fragments > count) {
            player.closeInventory()
            player.sendMessage(messageConfiguration.event.menu.needFragments)
            return
        }
        itemService.delete(player, ItemStrings.map, itemSection.fragments)
        player.closeInventory()
        player.sendMessage(messageConfiguration.event.menu.successfullyBuy
            .resolve(Placeholder.component("item-name", itemSection.template.displayName!!)))
        player.inventory.addItem(itemSection.template.toItem())
    }

    private fun resolve(): ItemStack {
        val count = itemService.count(player, ItemStrings.map)

        val additionalLore = buildList {
            addAll(shopConfiguration.showItemLore)

            if (count < itemSection.fragments) {
                val need = itemSection.fragments - count

                addAll(
                    shopConfiguration.notHaveFragmentsLore.map {
                        it.resolve(
                            Formatter.number("fragments", need)
                        )
                    }
                )
            } else {
                addAll(shopConfiguration.haveFragmentsLore)
            }
        }

        return itemSection.template
            .resolveLore(
                additionalLore,
                Formatter.number("fragments", itemSection.fragments)
            )
            .toItem()
    }
}