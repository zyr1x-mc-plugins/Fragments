package ru.lewis.fragments.api

import org.bukkit.Location

/**
 * Controls the in-world Fragments event lifecycle.
 *
 * The Fragments event is a scheduled, recurring in-world occurrence: it places a
 * schematic, spawns an NPC, protects the area with a WorldGuard region, and runs a tick
 * loop during which players inside the radius compete for exclusive purchase rights.
 *
 * All methods of this interface must be called on the main server thread.
 *
 * @see FragmentsApi.event
 */
interface FragmentEventController {

    /**
     * Returns whether the Fragments event is currently running.
     *
     * @return `true` if the event is active, `false` otherwise
     */
    fun isRunning(): Boolean

    /**
     * Returns the location where the event schematic was placed, or `null` if the event
     * is not running.
     *
     * @return the event location, or `null` when the event is not active
     */
    fun getLocation(): Location?

    /**
     * Starts the Fragments event if it is not already running.
     *
     * Starting is asynchronous internally: the schematic is located and pasted off the
     * main thread before the event actually boots. This method returns immediately; the
     * event becomes "running" shortly afterwards. Use [isRunning] to poll the state.
     *
     * If the event is already running, this is a no-op.
     *
     * Must be called on the main thread.
     */
    fun start()

    /**
     * Stops the Fragments event if it is currently running.
     *
     * This removes the placed schematic, the WorldGuard region and the spawned NPC, and
     * stops the tick loop. If the event is not running, this is a no-op.
     *
     * Must be called on the main thread.
     */
    fun stop()
}
