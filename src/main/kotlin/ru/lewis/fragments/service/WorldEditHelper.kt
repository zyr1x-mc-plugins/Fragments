package ru.lewis.fragments.service

import com.google.inject.Singleton
import com.sk89q.worldedit.EditSession
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.session.ClipboardHolder
import jakarta.inject.Inject
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.FileInputStream

@Singleton
class WorldEditHelper @Inject constructor(
    private val plugin: Plugin
) {

    fun createEditSession(world: World): EditSession {
        return WorldEdit.getInstance()
            .newEditSessionBuilder()
            .world(BukkitAdapter.adapt(world))
            .build()
    }

    fun createClipboard(file: File): Clipboard {
        if (!file.exists()) {
            error("Schematic file not found: ${file.absolutePath}")
        }

        FileInputStream(file).use { stream ->
            val format = ClipboardFormats.findByFile(file)
                ?: error("Unknown schematic format: ${file.name}")

            return format.getReader(stream).read()
        }
    }

    fun pasteClipboard(
        editSession: EditSession,
        clipboard: Clipboard,
        location: Location
    ) {
        // Смещение "нижнего центра" относительно origin клипборда (а не абсолютных координат!)
        val bottomCenterOffset = getBottomCenterOffsetFromOrigin(clipboard)

        val target = location.toBlockVector().subtract(bottomCenterOffset)

        plugin.logger.info(
            "[WorldEdit] origin=${clipboard.origin} min=${clipboard.region.minimumPoint} " +
                    "max=${clipboard.region.maximumPoint} offset=$bottomCenterOffset target=$target"
        )

        val operation = ClipboardHolder(clipboard)
            .createPaste(editSession)
            .to(target)
            .ignoreAirBlocks(false)
            .build()

        Operations.complete(operation)
        editSession.flushSession()

        plugin.logger.info("[WorldEdit] Изменено блоков: ${editSession.blockChangeCount}")
    }

    fun undo(editSession: EditSession) {
        val undoSession = WorldEdit.getInstance()
            .newEditSessionBuilder()
            .world(editSession.world)
            .build()

        try {
            editSession.undo(undoSession)
        } finally {
            undoSession.close()
        }
    }

    /**
     * Смещение точки "нижний центр региона" относительно origin клипборда.
     * origin — это точка (0,0,0) относительно которой WorldEdit позиционирует .to().
     */
    private fun getBottomCenterOffsetFromOrigin(clipboard: Clipboard): BlockVector3 {
        val min = clipboard.region.minimumPoint
        val max = clipboard.region.maximumPoint
        val origin = clipboard.origin

        return BlockVector3.at(
            (min.x() + max.x()) / 2 - origin.x(),
            min.y() - origin.y(),
            (min.z() + max.z()) / 2 - origin.z()
        )
    }

    private fun Location.toBlockVector(): BlockVector3 {
        return BlockVector3.at(blockX, blockY, blockZ)
    }
}