package ru.lewis.fragments.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import ru.lewis.fragments.config.type.BossBarConfiguration
import ru.lewis.fragments.config.type.NpcTemplate
import java.time.Duration

@ConfigSerializable
data class EventConfiguration(
    val npc: NpcTemplate = NpcTemplate(),
    val repeat: Duration = Duration.ofHours(3),
    val stayIt: Duration = Duration.ofMinutes(15),
    val location: Location = Location(),
    val schematicPath: String = "schematic/event.schem",
    val radius: Double = 15.0,
    val bossBar: BossBar = BossBar(),
) {
    @ConfigSerializable
    data class Location(
        val world: String = "world",
        val minX: Int = -1000,
        val maxX: Int = 1500,
        val minZ: Int = -1000,
        val maxZ: Int = 1500,
    )

    @ConfigSerializable
    data class BossBar(
        val green: BossBarConfiguration = BossBarConfiguration(),
        val red: BossBarConfiguration = BossBarConfiguration(color = net.kyori.adventure.bossbar.BossBar.Color.RED),
    )
}