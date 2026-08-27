package ru.lewis.fragments.api.impl

import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.bukkit.entity.Player
import ru.lewis.fragments.api.FragmentPurchaseController
import ru.lewis.fragments.model.PurchaseManager
import ru.lewis.fragments.model.menu.EventMenuFragments

@Singleton
class FragmentPurchaseControllerImpl @Inject constructor(
    private val purchaseManager: PurchaseManager,
    private val eventMenuFragments: EventMenuFragments,
) : FragmentPurchaseController {

    override fun isPurchaseEnabled(): Boolean = purchaseManager.purchase

    override fun openShop(player: Player) {
        eventMenuFragments.open(player)
    }
}
