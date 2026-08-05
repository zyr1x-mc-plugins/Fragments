package ru.lewis.fragments.service

import com.google.inject.Inject
import com.google.inject.Singleton
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

@Singleton
class ItemService @Inject constructor() {

    fun applyStrings(
        item: ItemStack,
        values: Map<String, String>
    ): ItemStack {
        val meta = item.itemMeta ?: return item

        val container = meta.persistentDataContainer

        values.forEach { (key, value) ->
            container.set(
                NamespacedKey(NAMESPACE, key),
                PersistentDataType.STRING,
                value
            )
        }

        item.itemMeta = meta

        return item
    }


    fun checkStrings(
        item: ItemStack,
        values: Map<String, String>
    ): Boolean {
        val meta = item.itemMeta ?: return false

        val container = meta.persistentDataContainer

        return values.all { (key, value) ->
            container.get(
                NamespacedKey(NAMESPACE, key),
                PersistentDataType.STRING
            ) == value
        }
    }


    fun count(
        player: Player,
        values: Map<String, String>
    ): Int {
        return player.inventory.contents
            .filterNotNull()
            .filter { checkStrings(it, values) }
            .sumOf { it.amount }
    }


    fun delete(
        player: Player,
        values: Map<String, String>,
        amount: Int
    ): Int {
        var remaining = amount

        val contents = player.inventory.contents

        for (index in contents.indices) {
            if (remaining <= 0) break

            val item = contents[index] ?: continue
            if (!checkStrings(item, values)) continue

            val remove = minOf(item.amount, remaining)
            val newAmount = item.amount - remove
            remaining -= remove

            if (newAmount <= 0) {
                player.inventory.setItem(index, null)
            } else {
                item.amount = newAmount
                player.inventory.setItem(index, item)
            }
        }
        player.updateInventory()

        return amount - remaining
    }

    companion object {
        private const val NAMESPACE = "lewis"
    }
}