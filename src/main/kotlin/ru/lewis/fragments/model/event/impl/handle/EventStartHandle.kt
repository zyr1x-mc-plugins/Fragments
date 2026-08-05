package ru.lewis.fragments.model.event.impl.handle

import jakarta.inject.Inject
import jakarta.inject.Singleton
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import ru.lewis.fragments.extensions.broadCast
import ru.lewis.fragments.extensions.broadCastSound
import ru.lewis.fragments.extensions.broadCastTitle
import ru.lewis.fragments.extensions.inTicks
import ru.lewis.fragments.model.event.impl.EventState
import ru.lewis.fragments.service.ConfigurationService
import ru.lewis.fragments.service.FancyNpcService
import ru.lewis.fragments.service.SchematicLocationService
import ru.lewis.fragments.service.WorldEditHelper
import ru.lewis.fragments.service.WorldGuardHelper
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.logging.Level

@Singleton
class EventStartHandle @Inject constructor(
    private val plugin: Plugin,
    private val configurationService: ConfigurationService,
    private val eventTickHandle: EventTickHandle,
    private val eventStopHandle: EventStopHandle,
    private val worldEditHelper: WorldEditHelper,
    private val schematicLocationService: SchematicLocationService,
    private val npcService: FancyNpcService,
    private val worldGuardHelper: WorldGuardHelper,
    private val state: EventState
) {
    private val eventSection get() = configurationService.eventConfigurationSection
    private val message get() = configurationService.messageConfigurationSection

    fun start() {
        if (state.isRunning) return
        state.running = true
        plugin.logger.info("[Event] start() вызван, running=true")

        try {
            placeSchematic()
                .orTimeout(30, TimeUnit.SECONDS) // <-- если зависнет, упадёт через 30 сек вместо вечного молчания
                .thenAccept {
                    Bukkit.getScheduler().runTask(plugin, Runnable {
                        try {
                            plugin.logger.info("[Event] Схематика размещена, запускаю остальное")
                            spawnNpc()
                            createRegion()
                            eventTickHandle.start(eventSection.stayIt.inTicks)
                            startTickScheduler()
                            startAutoStopScheduler()

                            val start = message.event.start
                            broadCast(
                                start.message.resolve(
                                    Formatter.number("x", state.location!!.blockX),
                                    Formatter.number("y", state.location!!.blockY),
                                    Formatter.number("z", state.location!!.blockZ)
                                )
                            )
                            broadCastTitle(start.title)
                            broadCastSound(start.sound)
                        } catch (exception: Exception) {
                            plugin.logger.log(Level.SEVERE, "[Event] Ошибка после размещения схематики", exception)
                            state.clear()
                        }
                    })
                }
                .exceptionally { throwable ->
                    plugin.logger.log(Level.SEVERE, "[Event] Не удалось запустить event (async)", throwable)
                    Bukkit.getScheduler().runTask(plugin, Runnable {
                        state.clear()
                    })
                    null
                }
        } catch (exception: Exception) {
            // ловим синхронные исключения, которые могли вылететь ДО создания CompletableFuture
            plugin.logger.log(Level.SEVERE, "[Event] Синхронная ошибка в start()", exception)
            state.clear()
        }
    }

    private fun placeSchematic(): CompletableFuture<Void> {
        val locationConfig = eventSection.location
        val world = Bukkit.getWorld(locationConfig.world)
            ?: throw NullPointerException("World '${locationConfig.world}' is null!")

        val file = plugin.dataPath.resolve(eventSection.schematicPath).toFile()
        plugin.logger.info("[Event] Загружаю схематику: ${file.absolutePath} (существует: ${file.exists()})")

        val clipboard = worldEditHelper.createClipboard(file)
        plugin.logger.info("[Event] Клипборд создан, размер региона: ${clipboard.region.width}x${clipboard.region.height}x${clipboard.region.length}")

        plugin.logger.info("[Event] Ищу случайную локацию в границах x[${locationConfig.minX}..${locationConfig.maxX}] z[${locationConfig.minZ}..${locationConfig.maxZ}]")

        return schematicLocationService
            .findRandomLocationAsync(
                plugin,
                world,
                clipboard,
                locationConfig.minX,
                locationConfig.maxX,
                locationConfig.minZ,
                locationConfig.maxZ
            )
            .thenCompose { generatedLocation ->
                plugin.logger.info("[Event] Локация найдена: $generatedLocation")

                if (generatedLocation == null) {
                    return@thenCompose CompletableFuture.failedFuture(
                        IllegalStateException("Не удалось найти место для схематики (все попытки заняты/невалидны)")
                    )
                }

                val future = CompletableFuture<Void>()

                Bukkit.getScheduler().runTask(plugin, Runnable {
                    try {
                        state.location = generatedLocation

                        val editSession = worldEditHelper.createEditSession(world)
                        state.editSession = editSession

                        worldEditHelper.pasteClipboard(
                            editSession,
                            clipboard,
                            generatedLocation
                        )

                        plugin.logger.info("[Event] Паста выполнена, изменено блоков: ${editSession.blockChangeCount}")

                        future.complete(null)
                    } catch (exception: Exception) {
                        future.completeExceptionally(exception)
                    }
                })
                future
            }
    }

    private fun spawnNpc() {
        npcService.spawn(eventSection.npc, state.location!!)
    }

    private fun createRegion() {
        state.region = UUID.randomUUID().toString()
        worldGuardHelper.createRegion(state.location!!, eventSection.radius.toInt(), state.region!!)
    }

    private fun startTickScheduler() {
        state.tickTask = Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            eventTickHandle.onTick(state.location!!)
        }, 20L, 20L)
    }

    private fun startAutoStopScheduler() {
        state.stopTask = Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            eventStopHandle.stop()
        }, eventSection.stayIt.inTicks)
    }
}