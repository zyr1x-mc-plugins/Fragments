package ru.lewis.fragments.api

import org.bukkit.entity.Player
import java.util.UUID

/**
 * Represents the Fragments economy subsystem: the balance of "fragments" a player owns.
 *
 * A player's balance is a separate, persistent counter (not the physical fragment items
 * held in the inventory, although the physical items can be converted into balance
 * through the event shop).
 *
 * All methods of this interface must be called on the main server thread. Methods are
 * blocking: they update the in-memory cache and persist the change to the database
 * before returning. There is no asynchronous variant.
 *
 * @see FragmentsApi.economy
 */
interface FragmentsEconomy {

    /**
     * Returns the number of fragments the given player owns.
     *
     * @param player the player whose balance is queried; must not be `null`
     * @return the current balance, always `>= 0`
     * @throws IllegalStateException if the fragments cache has not been initialised yet
     */
    fun getFragments(player: Player): Int

    /**
     * Returns the number of fragments the player with the given [uniqueId] owns.
     *
     * @param uniqueId the UUID of the player whose balance is queried; must not be `null`
     * @return the current balance, always `>= 0` (0 if the player has no balance yet)
     * @throws IllegalStateException if the fragments cache has not been initialised yet
     */
    fun getFragments(uniqueId: UUID): Int

    /**
     * Adds [amount] fragments to the given player's balance and persists the change.
     *
     * @param player the player whose balance is modified; must not be `null`
     * @param amount the number of fragments to add; must be `>= 0`
     * @return the new balance after the addition
     * @throws IllegalArgumentException if [amount] is negative
     * @throws IllegalStateException if the fragments cache has not been initialised yet
     */
    fun addFragments(player: Player, amount: Int): Int

    /**
     * Adds [amount] fragments to the balance of the player with the given [uniqueId] and
     * persists the change.
     *
     * @param uniqueId the UUID of the player whose balance is modified; must not be `null`
     * @param amount the number of fragments to add; must be `>= 0`
     * @return the new balance after the addition
     * @throws IllegalArgumentException if [amount] is negative
     * @throws IllegalStateException if the fragments cache has not been initialised yet
     */
    fun addFragments(uniqueId: UUID, amount: Int): Int

    /**
     * Removes [amount] fragments from the given player's balance and persists the change.
     *
     * The balance is clamped at `0`; it never becomes negative.
     *
     * @param player the player whose balance is modified; must not be `null`
     * @param amount the number of fragments to remove; must be `>= 0`
     * @return the new balance after the removal (clamped to `>= 0`)
     * @throws IllegalArgumentException if [amount] is negative
     * @throws IllegalStateException if the fragments cache has not been initialised yet
     */
    fun removeFragments(player: Player, amount: Int): Int

    /**
     * Removes [amount] fragments from the balance of the player with the given [uniqueId]
     * and persists the change.
     *
     * The balance is clamped at `0`; it never becomes negative.
     *
     * @param uniqueId the UUID of the player whose balance is modified; must not be `null`
     * @param amount the number of fragments to remove; must be `>= 0`
     * @return the new balance after the removal (clamped to `>= 0`)
     * @throws IllegalArgumentException if [amount] is negative
     * @throws IllegalStateException if the fragments cache has not been initialised yet
     */
    fun removeFragments(uniqueId: UUID, amount: Int): Int

    /**
     * Sets the given player's balance to [amount] and persists the change.
     *
     * @param player the player whose balance is set; must not be `null`
     * @param amount the new balance; must be `>= 0`
     * @return the new balance
     * @throws IllegalArgumentException if [amount] is negative
     * @throws IllegalStateException if the fragments cache has not been initialised yet
     */
    fun setFragments(player: Player, amount: Int): Int

    /**
     * Sets the balance of the player with the given [uniqueId] to [amount] and persists
     * the change.
     *
     * @param uniqueId the UUID of the player whose balance is set; must not be `null`
     * @param amount the new balance; must be `>= 0`
     * @return the new balance
     * @throws IllegalArgumentException if [amount] is negative
     * @throws IllegalStateException if the fragments cache has not been initialised yet
     */
    fun setFragments(uniqueId: UUID, amount: Int): Int

    /**
     * Returns the players with the highest balances, ordered descending by balance.
     *
     * @param limit the maximum number of entries to return; must be `>= 1`
     * @return a Map of player UUID to balance, ordered from highest to lowest, containing
     *         at most [limit] entries
     * @throws IllegalArgumentException if [limit] is less than `1`
     * @throws IllegalStateException if the fragments cache has not been initialised yet
     */
    fun getTop(limit: Int): Map<UUID, Int>
}
