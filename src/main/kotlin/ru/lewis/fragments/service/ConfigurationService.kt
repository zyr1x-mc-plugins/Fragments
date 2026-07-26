package ru.lewis.fragments.service

import com.google.inject.Inject
import com.google.inject.Singleton
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.sound.Sound
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType
import org.spongepowered.configurate.kotlin.extensions.get
import org.spongepowered.configurate.kotlin.extensions.set
import org.spongepowered.configurate.kotlin.objectMapperFactory
import org.spongepowered.configurate.yaml.NodeStyle
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import ru.lewis.fragments.config.Configuration
import ru.lewis.fragments.config.EventConfiguration
import ru.lewis.fragments.config.MessageConfiguration
import ru.lewis.fragments.config.ShopConfiguration
import ru.lewis.fragments.config.serializer.AttributeModifierSerializer
import ru.lewis.fragments.config.serializer.AttributeSerializer
import ru.lewis.fragments.config.serializer.BossBarColorSerializer
import ru.lewis.fragments.config.serializer.BossBarOverlaySerializer
import ru.lewis.fragments.config.serializer.ColorSerializer
import ru.lewis.fragments.config.serializer.DurationSerializer
import ru.lewis.fragments.config.serializer.EnchantmentSerializer
import ru.lewis.fragments.config.serializer.ItemFlagSerializer
import ru.lewis.fragments.config.serializer.MaterialSerializer
import ru.lewis.fragments.config.serializer.MiniMessageComponentSerializer
import ru.lewis.fragments.config.serializer.PotionEffectSerializer
import ru.lewis.fragments.config.serializer.PotionEffectTypeSerializer
import ru.lewis.fragments.config.serializer.PotionTypeSerializer
import ru.lewis.fragments.config.serializer.SoundSourceSerializer
import ru.lewis.fragments.config.type.MiniMessageComponent
import java.awt.Color
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.time.Duration

@Singleton
class ConfigurationService @Inject constructor(
    private val plugin: Plugin,
    private val materialSerializer: MaterialSerializer,
    private val miniMessageComponentSerializer: MiniMessageComponentSerializer,
    private val colorSerializer: ColorSerializer,
    private val potionEffectSerializer: PotionEffectSerializer,
    private val enchantmentSerializer: EnchantmentSerializer,
    private val attributeModifierSerializer: AttributeModifierSerializer,
    private val potionEffectTypeSerializer: PotionEffectTypeSerializer,
    private val itemFlagSerializer: ItemFlagSerializer,
    private val attributeSerializer: AttributeSerializer,
    private val potionTypeSerializer: PotionTypeSerializer,
    private val durationSerializer: DurationSerializer,
    private val soundSourceSerializer: SoundSourceSerializer,
    private val bossBarColorSerializer: BossBarColorSerializer,
    private val bossBarOverlaySerializer: BossBarOverlaySerializer,
) {
    private val defaultPath get(): Path = plugin.dataFolder.toPath()

    lateinit var configurationSection: Configuration
    lateinit var messageConfigurationSection: MessageConfiguration
    lateinit var eventConfigurationSection: EventConfiguration
    lateinit var shopConfigurationSection: ShopConfiguration

    fun run() {
        val builder = createLoaderBuilder()

        configurationSection = loadConfig(builder, "config")
        messageConfigurationSection = loadConfig(builder, "message")
        eventConfigurationSection = loadConfig(builder, "event")
        shopConfigurationSection = loadConfig(builder, "shop")
    }

    private fun createLoaderBuilder(): YamlConfigurationLoader.Builder {
        return YamlConfigurationLoader.builder()
            .defaultOptions { options ->
                options.serializers { builder ->
                    builder
                        .register(PotionType::class.java, potionTypeSerializer)
                        .register(Attribute::class.java, attributeSerializer)
                        .register(ItemFlag::class.java, itemFlagSerializer)
                        .register(MiniMessageComponent::class.java, miniMessageComponentSerializer)
                        .register(Material::class.java, materialSerializer)
                        .register(Color::class.java, colorSerializer)
                        .register(PotionEffect::class.java, potionEffectSerializer)
                        .register(Enchantment::class.java, enchantmentSerializer)
                        .register(Duration::class.java, durationSerializer)
                        .register(AttributeModifier::class.java, attributeModifierSerializer)
                        .register(PotionEffectType::class.java, potionEffectTypeSerializer)
                        .register(Sound.Source::class.java, soundSourceSerializer)
                        .register(BossBar.Color::class.java, bossBarColorSerializer)
                        .register(BossBar.Overlay::class.java, bossBarOverlaySerializer)
                        .registerAnnotatedObjects(objectMapperFactory())
                }
            }
            .indent(2)
            .nodeStyle(NodeStyle.BLOCK)
    }

    private inline fun <reified T : Any> YamlConfigurationLoader.getAndSave(): T {
        val node = this.load()

        var obj = node.get(T::class)

        if (obj == null) {
            plugin.logger.warning("Config ${T::class.simpleName} is empty, creating default...")
            obj = try {
                T::class.java.getDeclaredConstructor().newInstance()
            } catch (e: Exception) {
                throw IllegalStateException("Failed to create default config for ${T::class.simpleName}. Make sure class has a no-arg constructor or default values.", e)
            }
        }

        node.set(T::class, obj)
        this.save(node)
        return obj
    }

    private inline fun <reified T : Any> loadConfig(
        builder: YamlConfigurationLoader.Builder,
        name: String,
        subfolder: String? = null
    ): T {
        val path = if (subfolder != null) {
            defaultPath.resolve("$subfolder${File.separator}$name.yml")
        } else {
            defaultPath.resolve("$name.yml")
        }

        // Создаем родительские директории если не существуют
        path.parent?.let { Files.createDirectories(it) }

        val loader = builder.path(path).build()

        return try {
            loader.getAndSave()
        } catch (e: Exception) {
            plugin.logger.severe("Failed to load config: $name.yml")
            plugin.logger.severe("Error: ${e.message}")

            // Пытаемся создать дефолтный конфиг из ресурсов
            val resourcePath = if (subfolder != null) {
                "$subfolder/$name.yml"
            } else {
                "$name.yml"
            }

            plugin.logger.info("Attempting to create default config from resources: $resourcePath")

            try {
                plugin.getResource(resourcePath)?.use { input ->
                    Files.copy(input, path, StandardCopyOption.REPLACE_EXISTING)
                    plugin.logger.info("Created default config: $name.yml")
                    return loader.getAndSave()
                }
            } catch (resourceError: Exception) {
                plugin.logger.warning("No default config found in resources for: $resourcePath")
            }

            throw IllegalStateException("Failed to load config: $name.yml. Please create it manually or check the example configs.", e)
        }
    }
}