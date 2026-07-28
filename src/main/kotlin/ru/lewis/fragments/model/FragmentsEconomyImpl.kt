package ru.lewis.fragments.model

import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.bukkit.entity.Player
import ru.lewis.fragments.api.FragmentsEconomy
import ru.lewis.fragments.service.FragmentsService

@Singleton
class FragmentsEconomyImpl @Inject constructor(
    private val fragmentsService: FragmentsService
) : FragmentsEconomy {
    override fun addFragments(player: Player, count: Int) {
        fragmentsService.addCount(player.uniqueId, count)
    }

    override fun removeFragments(player: Player, count: Int) {
        fragmentsService.removeCount(player.uniqueId, count)
    }

    override fun setFragments(player: Player, count: Int) {
        fragmentsService.setCount(player.uniqueId, count)
    }
}