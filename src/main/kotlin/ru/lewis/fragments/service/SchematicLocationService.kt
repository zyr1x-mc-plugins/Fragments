package ru.lewis.fragments.service

import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.google.inject.Singleton
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.plugin.Plugin
import java.util.concurrent.CompletableFuture
import kotlin.random.Random

@Singleton
class SchematicLocationService {

    fun findRandomLocationAsync(
        plugin: Plugin,
        world: World,
        clipboard: Clipboard,
        minX: Int,
        maxX: Int,
        minZ: Int,
        maxZ: Int,
        attempts: Int = 100
    ): CompletableFuture<Location?> {
        return tryAttempt(plugin, world, clipboard, minX, maxX, minZ, maxZ, attempts)
    }

    private fun tryAttempt(
        plugin: Plugin,
        world: World,
        clipboard: Clipboard,
        minX: Int,
        maxX: Int,
        minZ: Int,
        maxZ: Int,
        attemptsLeft: Int
    ): CompletableFuture<Location?> {

        if (attemptsLeft <= 0) {
            return CompletableFuture.completedFuture(null)
        }

        val size = clipboard.dimensions
        val radiusX = size.x() / 2
        val radiusZ = size.z() / 2

        val x = Random.nextInt(minX + radiusX, maxX - radiusX)
        val z = Random.nextInt(minZ + radiusZ, maxZ - radiusZ)

        // 1. Асинхронно грузим все нужные чанки
        return preloadChunks(world, x, z, radiusX, radiusZ)
            // 2. Короткий прыжок в главный поток только для чтения блоков
            .thenCompose {
                callSync(plugin) {
                    checkAndBuildLocation(world, clipboard, x, z, radiusX, radiusZ)
                }
            }
            // 3. Если не подошло — рекурсивно пробуем ещё раз (тоже асинхронно)
            .thenCompose { location ->
                if (location != null) {
                    CompletableFuture.completedFuture(location)
                } else {
                    tryAttempt(plugin, world, clipboard, minX, maxX, minZ, maxZ, attemptsLeft - 1)
                }
            }
    }

    private fun preloadChunks(
        world: World,
        centerX: Int,
        centerZ: Int,
        radiusX: Int,
        radiusZ: Int
    ): CompletableFuture<Void> {

        val minChunkX = (centerX - radiusX) shr 4
        val maxChunkX = (centerX + radiusX) shr 4
        val minChunkZ = (centerZ - radiusZ) shr 4
        val maxChunkZ = (centerZ + radiusZ) shr 4

        val futures = mutableListOf<CompletableFuture<Chunk>>()

        for (cx in minChunkX..maxChunkX) {
            for (cz in minChunkZ..maxChunkZ) {
                // getChunkAtAsync можно звать из любого потока
                futures.add(world.getChunkAtAsync(cx, cz))
            }
        }

        return CompletableFuture.allOf(*futures.toTypedArray())
    }

    private fun <T> callSync(plugin: Plugin, block: () -> T): CompletableFuture<T> {
        val future = CompletableFuture<T>()
        Bukkit.getScheduler().runTask(plugin, Runnable {
            try {
                future.complete(block())
            } catch (e: Throwable) {
                future.completeExceptionally(e)
            }
        })
        return future
    }

    private fun checkAndBuildLocation(
        world: World,
        clipboard: Clipboard,
        x: Int,
        z: Int,
        radiusX: Int,
        radiusZ: Int
    ): Location? {
        val y = world.getHighestBlockYAt(x, z)
        val location = Location(world, x.toDouble(), y.toDouble(), z.toDouble())
        return if (checkArea(location, clipboard)) location else null
    }

    private fun checkArea(center: Location, clipboard: Clipboard): Boolean {
        val world = center.world ?: return false
        val size = clipboard.dimensions
        val halfX = size.x() / 2
        val halfZ = size.z() / 2

        for (x in -halfX..halfX) {
            for (z in -halfZ..halfZ) {
                val blockX = center.blockX + x
                val blockZ = center.blockZ + z
                val y = world.getHighestBlockYAt(blockX, blockZ)
                val ground = world.getBlockAt(blockX, y, blockZ)
                if (!isGoodGround(ground.type)) return false
            }
        }
        return true
    }

    private fun isGoodGround(material: Material): Boolean {
        return material.isSolid &&
                material != Material.WATER &&
                material != Material.LAVA
    }
}