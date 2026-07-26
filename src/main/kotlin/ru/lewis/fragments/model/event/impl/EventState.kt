package ru.lewis.fragments.model.event.impl

import com.sk89q.worldedit.EditSession
import org.bukkit.Location
import org.bukkit.scheduler.BukkitTask
import jakarta.inject.Singleton

@Singleton
class EventState {
    var running = false

    var location: Location? = null
    var editSession: EditSession? = null
    var region: String? = null

    var tickTask: BukkitTask? = null
    var stopTask: BukkitTask? = null


    val isRunning: Boolean
        get() = running


    fun clear() {
        tickTask?.cancel()
        stopTask?.cancel()

        tickTask = null
        stopTask = null

        location = null
        editSession = null
        region = null

        running = false
    }
}