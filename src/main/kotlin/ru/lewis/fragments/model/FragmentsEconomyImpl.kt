package ru.lewis.fragments.model

import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.bukkit.entity.Player
import ru.lewis.fragments.api.FragmentsEconomy
import ru.lewis.fragments.service.FragmentsService
import java.util.UUID

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

    override fun addFragments(uniqueId: UUID, count: Int) {
        fragmentsService.addCount(uniqueId, count)
    }

    override fun removeFragments(uniqueId: UUID, count: Int) {
        fragmentsService.removeCount(uniqueId, count)
    }

    override fun setFragments(uniqueId: UUID, count: Int) {
        fragmentsService.setCount(uniqueId, count)
    }
}