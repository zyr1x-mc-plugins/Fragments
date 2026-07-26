package ru.lewis.fragments.config

import org.bukkit.Material
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import ru.lewis.fragments.config.type.ItemTemplate
import ru.lewis.fragments.config.type.MenuConfig
import ru.lewis.fragments.config.type.MiniMessageComponent
import ru.lewis.fragments.extensions.asMiniMessageComponent

@ConfigSerializable
data class ShopConfiguration(
    val menuTemplate: MenuConfig = MenuConfig(
        title = "<gold>Меню осколков".asMiniMessageComponent(),
        structure = listOf(
            "x . . . . . . . x",
            "x x x x T x x x x"),
        customItems = mapOf(
            'x' to ItemTemplate(Material.GRAY_STAINED_GLASS_PANE),
        ),
    ),
    val items: List<ItemSection> = listOf(ItemSection()),
    val showItemLore: List<MiniMessageComponent> = listOf(
        "Этот предмет продается за <fragments> осколков".asMiniMessageComponent()
    ),
    val notHaveFragmentsLore: List<MiniMessageComponent> = listOf(
        "<red>У вас недостаточно осколков, нужно еще <fragments>".asMiniMessageComponent()
    ),
    val haveFragmentsLore: List<MiniMessageComponent> = listOf(
        "<green>У вас достаточно осколков для покупки".asMiniMessageComponent(),
        "<green>Нажмите, чтобы купить".asMiniMessageComponent()
    )
) {
    @ConfigSerializable
    data class ItemSection(
        val template: ItemTemplate = ItemTemplate(Material.DIAMOND_SWORD),
        val fragments: Int = 100
    )
}