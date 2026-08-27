package ru.lewis.fragments.service

import com.google.inject.Inject
import com.google.inject.Singleton
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import ru.lewis.fragments.api.event.FragmentBalanceUpdateEvent
import ru.lewis.fragments.model.FragmentsUserEntity
import ru.lewis.fragments.repository.FragmentsRepository
import ru.lewis.point.api.PointAPI
import java.util.UUID

@Singleton
class FragmentsService @Inject constructor(
    private val repository: FragmentsRepository,
    private val pointAPI: PointAPI,
    private val plugin: Plugin
) {

    private companion object {
        const val CACHE_KEY = "fragments:cache"
    }

    private fun getCache() =
        pointAPI.redisService.client.getMapCache<UUID, Int>(CACHE_KEY)

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

    fun setCount(uuid: UUID, count: Int): Int {
        require(count >= 0) { "count must be >= 0" }
        checkCache()

        val cache = getCache()
        val old = cache[uuid] ?: 0

        cache[uuid] = count

        saveToDatabase(uuid, count)

        fireBalanceUpdate(uuid, old, count)

        return count
    }

    fun addCount(uuid: UUID, amount: Int): Int {
        require(amount >= 0) { "amount must be >= 0" }
        checkCache()

        val cache = getCache()

        val oldCount = cache[uuid] ?: 0
        val newCount = oldCount + amount

        cache[uuid] = newCount

        saveToDatabase(uuid, newCount)

        fireBalanceUpdate(uuid, oldCount, newCount)

        return newCount
    }

    fun removeCount(uuid: UUID, amount: Int): Int {
        require(amount >= 0) { "amount must be >= 0" }
        checkCache()

        val cache = getCache()

        val oldCount = cache[uuid] ?: 0
        val newCount = (oldCount - amount).coerceAtLeast(0)

        cache[uuid] = newCount

        saveToDatabase(uuid, newCount)

        fireBalanceUpdate(uuid, oldCount, newCount)

        return newCount
    }

    fun getTop(limit: Int): Map<UUID, Int> {
        require(limit >= 1) { "limit must be >= 1" }
        checkCache()

        return getCache()
            .entries
            .sortedByDescending { it.value }
            .take(limit)
            .associate { it.key to it.value }
    }

    private fun saveToDatabase(uuid: UUID, count: Int) {
        val entity = repository.findById(uuid)
            ?: FragmentsUserEntity(uuid = uuid)

        entity.count = count

        repository.save(entity)
    }

    private fun fireBalanceUpdate(uuid: UUID, oldBalance: Int, newBalance: Int) {
        val player: Player? = Bukkit.getPlayer(uuid)
        Bukkit.getPluginManager().callEvent(
            FragmentBalanceUpdateEvent(player, uuid, oldBalance, newBalance)
        )
    }
}
