package ru.lewis.fragments.api.event

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import java.util.UUID

/**
 * Called after a player's fragment balance has changed.
 *
 * This event is fired on the main server thread, synchronously, immediately after the
 * balance has been updated and persisted. It is a notification event and cannot be
 * cancelled.
 *
 * The event carries both the old and the new balance, so listeners can compute the delta
 * even when the new balance is clamped (e.g. by
 * [ru.lewis.fragments.api.FragmentsEconomy.removeFragments]).
 *
 * @property player the player whose balance changed; may be `null` if the player is
 *   offline at the moment of the change (changes made by UUID)
 * @property uniqueId the UUID of the player whose balance changed
 * @property oldBalance the balance before the change
 * @property newBalance the balance after the change
 */
class FragmentBalanceUpdateEvent(
    val player: Player?,
    val uniqueId: UUID,
    val oldBalance: Int,
    val newBalance: Int,
) : Event() {

    /**
     * The absolute difference between the new and the old balance.
     */
    val delta: Int
        get() = newBalance - oldBalance

    override fun getHandlers(): HandlerList = HANDLERS

    companion object {
        private val HANDLERS = HandlerList()
        @JvmStatic
        fun getHandlerList(): HandlerList = HANDLERS
    }
}
