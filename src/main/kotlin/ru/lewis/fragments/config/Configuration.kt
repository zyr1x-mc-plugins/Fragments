package ru.lewis.fragments.config

import org.bukkit.Material
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import ru.lewis.fragments.config.type.ItemTemplate

@ConfigSerializable
data class Configuration(
    val fragmentItem: ItemTemplate = ItemTemplate(Material.PRISMARINE_SHARD),
)