package ru.lewis.fragments.service

import com.google.inject.Inject
import com.google.inject.Singleton
import org.bukkit.plugin.Plugin
import org.hibernate.SessionFactory
import org.mariadb.jdbc.Driver
import ru.lewis.fragments.builder.SessionFactoryBuilder
import ru.lewis.fragments.model.UserEntity
import kotlin.collections.set
import kotlin.time.Duration.Companion.seconds

/**
 * This class is necessary to establish a connection with the database
 */
@Singleton
class DatabaseService @Inject constructor(
    private val configurationService: ConfigurationService,
    private val plugin: Plugin
) {
    lateinit var sessionFactory: SessionFactory
    private val databaseData get() = configurationService.configurationSection.databaseData

    fun init() {
        sessionFactory = connect()
    }

    private fun connect(): SessionFactory {
        return SessionFactoryBuilder.build {
            classLoader = plugin::class.java.classLoader

            user = databaseData.user
            password = databaseData.password
            driver = Driver::class
            url = "jdbc:mariadb://${databaseData.address}/${databaseData.database}${parametersToString(databaseData.parameters)}"

            hikariProperties["maximumPoolSize"] = Runtime.getRuntime().availableProcessors().toString()
            hikariProperties["connectionTimeout"] = 10.seconds.inWholeMilliseconds.toString()
            hikariProperties["poolName"] = plugin.name.plus("/mariadb")

            register<UserEntity>()
        }
    }

    private fun parametersToString(parameters: List<String>): String {
        return parameters.joinToString(prefix = "?", separator = "&")
    }
}