package ru.lewis.fragments.config

import org.bukkit.Material
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import ru.lewis.fragments.config.type.DatabaseData
import ru.lewis.fragments.config.type.ItemTemplate
import ru.lewis.fragments.config.type.RedisData

@ConfigSerializable
data class Configuration(
    val databaseData: DatabaseData = DatabaseData(),
    val redisData: RedisData = RedisData(),
    val fragmentItem: ItemTemplate = ItemTemplate(Material.PRISMARINE_SHARD),
)