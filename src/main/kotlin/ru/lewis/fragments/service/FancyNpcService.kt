package ru.lewis.fragments.service

import de.oliver.fancynpcs.api.FancyNpcsPlugin
import de.oliver.fancynpcs.api.NpcData
import jakarta.inject.Singleton
import org.bukkit.Location
import ru.lewis.fragments.config.type.NpcTemplate
import java.util.*


@Singleton
class FancyNpcService {
    companion object {
        const val NPC_DATA_KEY = "fragments-event-npc"
    }

    fun spawn(npcTemplate: NpcTemplate, location: Location) = createData(npcTemplate, location)

    fun delete() {
        val npc = FancyNpcsPlugin.get().npcManager.getNpc(NPC_DATA_KEY)

        npc.removeForAll()
        FancyNpcsPlugin.get().npcManager.removeNpc(npc)
    }

    private fun createData(npcTemplate: NpcTemplate, location: Location): NpcData {
        val offsetLocation = location.clone().add(npcTemplate.deviation.x, npcTemplate.deviation.y, npcTemplate.deviation.z)
        val data = NpcData(NPC_DATA_KEY, UUID.randomUUID(), offsetLocation)
        data.setSkin(npcTemplate.skin)
        data.displayName = npcTemplate.displayName

        val npc = FancyNpcsPlugin.get().npcAdapter.apply(data)
        FancyNpcsPlugin.get().npcManager.registerNpc(npc)
        npc.create()
        npc.spawnForAll()

        return data
    }
}