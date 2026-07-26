package ru.lewis.fragments.config.type

import com.destroystokyo.paper.profile.ProfileProperty
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionType
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import ru.lewis.fragments.extensions.asMiniMessageComponent
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper
import xyz.xenondevs.invui.item.builder.AbstractItemBuilder
import xyz.xenondevs.invui.item.builder.ItemBuilder
import xyz.xenondevs.invui.item.builder.PotionBuilder
import java.awt.Color
import java.util.UUID

@ConfigSerializable
data class ItemTemplate(
    val type: Material,
    val amount: Int? = null,
    val displayName: MiniMessageComponent? = null,
    val lore: List<MiniMessageComponent>? = null,
    val flags: Set<ItemFlag>? = null,
    val enchantments: Map<Enchantment, Int>? = null,
    val unbreakable: Boolean? = null,
    val potion: PotionTemplate? = null,
    val skullTexture: String? = null,
    val attributes: List<AttributeConfiguration>? = null,
    val customModelData: Int? = null,
) {

    @ConfigSerializable
    data class PotionTemplate(
        val type: PotionType,
        val color: Color?,
        val effects: List<PotionEffect>?
    )

    fun resolve(vararg tagResolvers: TagResolver): ItemTemplate {
        return copy(
            displayName = displayName?.resolve(*tagResolvers),
            lore = lore?.map { line ->
                (line as MiniMessageComponent?)?.resolve(*tagResolvers) ?: Component.empty().asMiniMessageComponent()
            }
        )
    }

    fun resolveLore(additionalLore: List<MiniMessageComponent>, vararg tagResolvers: TagResolver): ItemTemplate {
        val resolvedAdditionalLore = additionalLore.map { it.resolve(*tagResolvers) }
        val resolvedOriginalLore = (lore ?: emptyList()).map { it.resolve(*tagResolvers) }
        return copy(
            displayName = displayName?.resolve(*tagResolvers),
            lore = resolvedOriginalLore + resolvedAdditionalLore
        )
    }

    /**
     * Создаёт чистый ванильный ItemStack БЕЗ InvUI builder.
     */
    fun toCleanItem(): ItemStack {
        val item = ItemStack(type, amount ?: 1)
        val meta = item.itemMeta ?: return item

        displayName?.let { meta.displayName(it.asComponent()) }

        lore?.let { loreList ->
            meta.lore(loreList.map { it.asComponent() })
        }

        enchantments?.forEach { (enchantment, level) ->
            if (meta is EnchantmentStorageMeta) {
                meta.addStoredEnchant(enchantment, level, true)
            } else {
                meta.addEnchant(enchantment, level, true)
            }
        }

        flags?.forEach { meta.addItemFlags(it) }

        if (unbreakable == true) {
            meta.isUnbreakable = true
        }

        if (potion != null && meta is PotionMeta) {
            meta.basePotionType = potion.type
            potion.color?.let {
                meta.color = org.bukkit.Color.fromRGB(it.red, it.green, it.blue)
            }
            potion.effects?.forEach { effect ->
                meta.addCustomEffect(effect, true)
            }
        }

        customModelData?.let { meta.setCustomModelData(it) }

        attributes?.forEach {
            meta.addAttributeModifier(it.attribute, it.modifier)
        }

        item.itemMeta = meta

        if (skullTexture != null) {
            setSkullTexture(item, skullTexture)
        }

        return item
    }

    /**
     * Создаёт ItemStack через InvUI builder (для GUI отображения).
     */
    fun toItem(): ItemStack {
        val builder = createBuilder()

        builder.amount = amount ?: 1

        displayName?.also { builder.setDisplayName(AdventureComponentWrapper(it.asComponent())) }

        if (lore != null) {
            builder.setLore(lore.map { AdventureComponentWrapper(it.asComponent()) })
        }

        if (flags != null) {
            builder.setItemFlags(flags.toMutableList())
        }

        enchantments?.forEach { (enchantment, level) ->
            builder.addEnchantment(enchantment, level, true)
        }

        builder.setUnbreakable(unbreakable ?: false)

        if (potion != null && builder is PotionBuilder) {
            potion.color?.let { builder.setColor(it) }
            potion.effects?.forEach { builder.addEffect(it) }
        }

        customModelData?.let { builder.setCustomModelData(it) }

        val item = builder.get()

        // Применяем basePotionType через itemMeta (PotionBuilder не поддерживает 1.21 API)
        if (potion != null) {
            val meta = item.itemMeta
            if (meta is PotionMeta) {
                meta.basePotionType = potion.type
                item.itemMeta = meta
            }
        }
        if (skullTexture != null) {
            setSkullTexture(item, skullTexture)
        }

        if (attributes != null) {
            item.itemMeta = item.itemMeta.apply {
                attributes.forEach {
                    item.itemMeta.addAttributeModifier(it.attribute, it.modifier)
                }
            }
        }

        return item
    }

    private fun createBuilder(): AbstractItemBuilder<*> {
        return when (type) {
            Material.POTION -> PotionBuilder(PotionBuilder.PotionType.NORMAL)
            Material.SPLASH_POTION -> PotionBuilder(PotionBuilder.PotionType.SPLASH)
            Material.LINGERING_POTION -> PotionBuilder(PotionBuilder.PotionType.LINGERING)
            Material.TIPPED_ARROW -> PotionBuilder(ItemStack(Material.TIPPED_ARROW))
            else -> ItemBuilder(type)
        }
    }

    private fun setSkullTexture(itemStack: ItemStack, skullTexture: String?) {
        if (skullTexture == null) return
        val itemMeta = itemStack.itemMeta

        if (itemMeta is SkullMeta) {
            val profile = Bukkit.createProfile(UUID.randomUUID())
            profile.setProperty(ProfileProperty("textures", skullTexture))
            itemMeta.playerProfile = profile
        }
        itemStack.setItemMeta(itemMeta)
    }
}
