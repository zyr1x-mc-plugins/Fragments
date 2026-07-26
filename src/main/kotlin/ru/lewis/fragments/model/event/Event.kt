package ru.lewis.fragments.model.event

import com.google.inject.ImplementedBy
import com.sk89q.worldedit.EditSession
import org.bukkit.Location
import ru.lewis.fragments.model.event.impl.EventImpl

@ImplementedBy(EventImpl::class)
interface Event {
    fun start()
    fun onTick(location: Location)
    fun stop()
}