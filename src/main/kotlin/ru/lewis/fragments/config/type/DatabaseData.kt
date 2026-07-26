package ru.lewis.fragments.config.type

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class DatabaseData(
    val address: String = "127.0.0.1:3306",
    val user: String = "fragments",
    val password: String = "your_password",
    val database: String = "your_database",
    val parameters: List<String> = listOf("useServerPrepStmts=true")
)