package ru.lewis.fragments.service

import com.google.inject.Inject
import com.google.inject.Singleton
import ru.lewis.fragments.model.UserEntity
import ru.lewis.fragments.repository.FragmentsRepository
import java.util.UUID

@Singleton
class FragmentsService @Inject constructor(
    private val repository: FragmentsRepository,
    private val redisService: RedisService
) {

    private companion object {
        const val CACHE_KEY = "fragments:cache"
    }

    private fun getCache() =
        redisService.getClient()
            .getMapCache<UUID, Int>(CACHE_KEY)

    private fun checkCache() {
        val cache = getCache()

        if (cache.isNotEmpty()) {
            return
        }

        repository.findAll().forEach { entity ->
            cache[entity.uuid!!] = entity.count
        }
    }

    fun loadCache() = checkCache()

    fun getCount(uuid: UUID): Int {
        checkCache()

        return getCache()[uuid] ?: 0
    }

    fun setCount(uuid: UUID, count: Int) {
        checkCache()

        val cache = getCache()
        cache[uuid] = count

        saveToDatabase(uuid, count)
    }

    fun addCount(uuid: UUID, amount: Int): Int {
        checkCache()

        val cache = getCache()

        val newCount = (cache[uuid] ?: 0) + amount

        cache[uuid] = newCount

        saveToDatabase(uuid, newCount)

        return newCount
    }

    fun removeCount(uuid: UUID, amount: Int): Int {
        checkCache()

        val cache = getCache()

        val newCount = ((cache[uuid] ?: 0) - amount)
            .coerceAtLeast(0)

        cache[uuid] = newCount

        saveToDatabase(uuid, newCount)

        return newCount
    }

    fun getTop(limit: Int): Map<UUID, Int> {
        checkCache()

        return getCache()
            .entries
            .sortedByDescending { it.value }
            .take(limit)
            .associate { it.key to it.value }
    }

    private fun saveToDatabase(uuid: UUID, count: Int) {
        val entity = repository.findById(uuid)
            ?: UserEntity(uuid = uuid)

        entity.count = count

        repository.save(entity)
    }
}