package ru.lewis.fragments.service

import com.google.inject.Singleton
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import ru.lewis.fragments.config.type.BossBarConfiguration

@Singleton
class BossBarService {

    private val miniMessage = MiniMessage.miniMessage()

    private var bossBar: BossBar? = null


    fun create(
        config: BossBarConfiguration
    ) {
        bossBar = BossBar.bossBar(
            config.title,
            1f,
            config.color,
            config.overlay
        )
    }


    fun show(
        player: Player
    ) {
        bossBar?.let {
            player.showBossBar(it)
        }
    }


    fun update(
        config: BossBarConfiguration,
        progress: Float
    ) {
        val bar = bossBar ?: return

        bar.name(
            config.title
        )

        bar.color(config.color)
        bar.overlay(config.overlay)
        bar.progress(
            progress.coerceIn(0f, 1f)
        )
    }


    fun hide(
        player: Player
    ) {
        bossBar?.let {
            player.hideBossBar(it)
        }
    }


    fun hideAll(
        players: Collection<Player>
    ) {
        players.forEach {
            hide(it)
        }
    }


    fun remove() {
        bossBar = null
    }
}