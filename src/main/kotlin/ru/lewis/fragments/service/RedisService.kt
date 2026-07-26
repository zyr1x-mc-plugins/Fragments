package ru.lewis.fragments.service

import com.google.inject.Inject
import com.google.inject.Singleton
import org.redisson.Redisson
import org.redisson.config.Config
import org.redisson.api.RedissonClient

@Singleton
class RedisService @Inject constructor(
    private val configurationService: ConfigurationService
) {
    private var client: RedissonClient? = null

    fun getClient(): RedissonClient {
        if (client == null) {
            client = createClient()
        }
        return client!!
    }

    private fun createClient(): RedissonClient {
        val redissonConfiguration = configurationService.configurationSection.redisData
        val config = Config()

        val singleServerConfig = config.useSingleServer()
            .setAddress("redis://${redissonConfiguration.address}")
            .setDatabase(0)
            .setConnectionMinimumIdleSize(5)
            .setConnectionPoolSize(10)
            .setTimeout(3000)
            .setDnsMonitoringInterval(-1)

        if (redissonConfiguration.password.isNotEmpty()) {
            singleServerConfig.password = redissonConfiguration.password
        }

        return Redisson.create(config)
    }

    fun shutdown() {
        client?.shutdown()
    }
}
