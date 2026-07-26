package ru.lewis.fragments.config.type

import com.google.inject.Singleton
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@Singleton
@ConfigSerializable
data class RedisData(
    val address: String = "127.0.0.1:6379",
    val password: String = ""
)