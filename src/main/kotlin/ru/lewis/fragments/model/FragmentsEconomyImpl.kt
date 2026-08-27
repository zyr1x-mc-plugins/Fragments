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
    override fun getFragments(player: Player): Int =
        fragmentsService.getCount(player.uniqueId)

    override fun getFragments(uniqueId: UUID): Int =
        fragmentsService.getCount(uniqueId)

    override fun addFragments(player: Player, amount: Int): Int =
        fragmentsService.addCount(player.uniqueId, amount)

    override fun addFragments(uniqueId: UUID, amount: Int): Int =
        fragmentsService.addCount(uniqueId, amount)

    override fun removeFragments(player: Player, amount: Int): Int =
        fragmentsService.removeCount(player.uniqueId, amount)

    override fun removeFragments(uniqueId: UUID, amount: Int): Int =
        fragmentsService.removeCount(uniqueId, amount)

    override fun setFragments(player: Player, amount: Int): Int =
        fragmentsService.setCount(player.uniqueId, amount)

    override fun setFragments(uniqueId: UUID, amount: Int): Int =
        fragmentsService.setCount(uniqueId, amount)

    override fun getTop(limit: Int): Map<UUID, Int> =
        fragmentsService.getTop(limit)
}
