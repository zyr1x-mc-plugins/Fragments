package ru.lewis.fragments.model.menu

import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.bukkit.entity.Player
import ru.lewis.fragments.config.type.import
import ru.lewis.fragments.model.PurchaseManager
import ru.lewis.fragments.model.menu.item.PassItem
import ru.lewis.fragments.model.menu.item.PurchasableItem
import ru.lewis.fragments.service.ConfigurationService
import ru.lewis.fragments.service.FragmentsService
import ru.lewis.fragments.service.ItemService
import xyz.xenondevs.invui.window.Window

@Singleton
class EventMenuFragments @Inject constructor(
    private val configurationService: ConfigurationService,
    private val itemService: ItemService,
    private val purchaseManager: PurchaseManager,
    private val fragmentService: FragmentsService
) {
    private val shopSection get() = configurationService.shopConfigurationSection
    private val messageSection get() = configurationService.messageConfigurationSection

    fun open(player: Player) {
        Window.single().apply {
            import(player, shopSection.menuTemplate) {
                setContent(
                    shopSection.items.map { itemSection ->
                        PurchasableItem(
                            player, itemSection, itemService,
                            shopSection, messageSection, purchaseManager
                        )
                    }
                )

                addIngredient('T', PassItem(shopSection.menuTemplate, itemService,
                    fragmentService, purchaseManager, messageSection))
            }

            open(player)
        }
    }
}