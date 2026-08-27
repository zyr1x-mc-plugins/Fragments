package ru.lewis.fragments.api

import org.bukkit.entity.Player

/**
 * Exposes the purchase state of the Fragments event shop and the ability to open the
 * shop for a player.
 *
 * Purchases (buying items and converting physical fragments into balance) are only
 * allowed while a single "exclusive" player is present inside the event area. This
 * controller lets integrations query that state and open the shop menu directly.
 *
 * All methods of this interface must be called on the main server thread.
 *
 * @see FragmentsApi.purchases
 */
interface FragmentPurchaseController {

    /**
     * Returns whether purchases are currently allowed.
     *
     * Purchases are allowed when the Fragments event is running and exactly one player is
     * present inside the event area.
     *
     * @return `true` if the player may purchase, `false` otherwise
     */
    fun isPurchaseEnabled(): Boolean

    /**
     * Opens the Fragments event shop menu for the given [player].
     *
     * @param player the player for whom the shop is opened; must not be `null` and must
     *               be online
     */
    fun openShop(player: Player)
}
