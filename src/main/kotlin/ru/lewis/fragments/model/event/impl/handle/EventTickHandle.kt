package ru.lewis.fragments.model.event.impl.handle

import jakarta.inject.Inject
import jakarta.inject.Singleton
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Location
import org.bukkit.entity.Player
import ru.lewis.fragments.extensions.toMinutesSeconds
import ru.lewis.fragments.model.PurchaseManager
import ru.lewis.fragments.service.BossBarService
import ru.lewis.fragments.service.ConfigurationService
import java.time.Duration

@Singleton
class EventTickHandle @Inject constructor(
    private val purchaseManager: PurchaseManager,
    private val configurationService: ConfigurationService,
    private val bossBarService: BossBarService,
) {
    private val eventSection get() = configurationService.eventConfigurationSection
    private val radius get() = eventSection.radius

    private var lastPlayers: Set<Player> = emptySet()
    private var endTime: Long = 0L
    private var durationMillis: Long = 0L

    fun start(durationTicks: Long) {
        lastPlayers = emptySet()
        durationMillis = durationTicks * 50L
        endTime = System.currentTimeMillis() + durationMillis
        bossBarService.create(eventSection.bossBar.green)
    }

    fun onTick(location: Location) {
        val players = getNearbyPlayers(location)
        val isExclusive = players.size == 1

        handleLeftPlayers(players)
        handleJoinedPlayers(players)
        updateBossBar(players, isExclusive)
        purchaseManager.purchase = isExclusive

        lastPlayers = players
    }

    private fun getNearbyPlayers(location: Location): Set<Player> {
        return location.getNearbyPlayers(radius).toSet()
    }

    private fun handleLeftPlayers(players: Set<Player>) {
        (lastPlayers - players).forEach { bossBarService.hide(it) }
    }

    private fun handleJoinedPlayers(players: Set<Player>) {
        (players - lastPlayers).forEach { bossBarService.show(it) }
    }

    private fun updateBossBar(players: Set<Player>, isExclusive: Boolean) {
        if (players.isEmpty()) return

        val bossBarConfig = if (isExclusive) eventSection.bossBar.green else eventSection.bossBar.red
        val remaining = remainingMillis()
        val progress = if (durationMillis > 0) {
            (remaining.toFloat() / durationMillis).coerceIn(0f, 1f)
        } else {
            0f
        }

        val resolvedBossBar = bossBarConfig.copy(
            title = bossBarConfig.title.resolve(
                Placeholder.unparsed("time", Duration.ofMillis(remaining).toMinutesSeconds())
            )
        )

        bossBarService.update(resolvedBossBar, progress)
    }

    private fun remainingMillis(): Long =
        (endTime - System.currentTimeMillis()).coerceAtLeast(0)

    fun clear() {
        lastPlayers.forEach { bossBarService.hide(it) }
        lastPlayers = emptySet()
        endTime = 0L
        durationMillis = 0L
        purchaseManager.purchase = false
    }
}