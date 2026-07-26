package ru.lewis.fragments.service

import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.google.inject.Singleton
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import java.util.concurrent.CompletableFuture
import kotlin.random.Random

@Singleton
class SchematicLocationService {

    fun findRandomLocationAsync(
        world: World,
        clipboard: Clipboard?,
        minX: Int,
        maxX: Int,
        minZ: Int,
        maxZ: Int
    ): CompletableFuture<Location?> {

        return CompletableFuture.supplyAsync {
            findRandomLocation(
                world,
                clipboard!!,
                minX,
                maxX,
                minZ,
                maxZ
            )
        }
    }

    fun findRandomLocation(
        world: World,
        clipboard: Clipboard,
        minX: Int,
        maxX: Int,
        minZ: Int,
        maxZ: Int,
        attempts: Int = 100
    ): Location? {

        val size = clipboard.dimensions

        val radiusX = size.x() / 2
        val radiusZ = size.z() / 2


        repeat(attempts) {

            val x = Random.nextInt(
                minX + radiusX,
                maxX - radiusX
            )

            val z = Random.nextInt(
                minZ + radiusZ,
                maxZ - radiusZ
            )


            val y = world.getHighestBlockYAt(x, z)


            val location = Location(
                world,
                x.toDouble(),
                y.toDouble(),
                z.toDouble()
            )


            if (checkArea(location, clipboard)) {
                return location
            }
        }


        return null
    }


    private fun checkArea(
        center: Location,
        clipboard: Clipboard
    ): Boolean {

        val world = center.world ?: return false

        val size = clipboard.dimensions


        val halfX = size.x() / 2
        val halfZ = size.z() / 2


        for (x in -halfX..halfX) {
            for (z in -halfZ..halfZ) {

                val blockX = center.blockX + x
                val blockZ = center.blockZ + z

                val y = world.getHighestBlockYAt(
                    blockX,
                    blockZ
                )

                val ground = world.getBlockAt(
                    blockX,
                    y,
                    blockZ
                )


                if (!isGoodGround(ground.type)) {
                    return false
                }
            }
        }

        return true
    }


    private fun isGoodGround(
        material: Material
    ): Boolean {
        return material.isSolid &&
                material != Material.WATER &&
                material != Material.LAVA
    }
}