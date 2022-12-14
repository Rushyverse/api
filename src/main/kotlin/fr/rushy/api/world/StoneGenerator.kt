package fr.rushy.api.world

import net.minestom.server.instance.block.Block
import net.minestom.server.instance.generator.GenerationUnit
import net.minestom.server.instance.generator.Generator

/**
 * Generator to create a flat chunk with [stone][Block.STONE].
 */
public class StoneGenerator : Generator {

    override fun generate(unit: GenerationUnit) {
        val modifier = unit.modifier()
        val start = unit.absoluteStart()
        val end = unit.absoluteEnd()

        for (x in start.blockX() until end.blockX()) {
            for (z in start.blockZ() until end.blockZ()) {
                modifier.setBlock(x, 0, z, Block.STONE)
            }
        }
    }
}