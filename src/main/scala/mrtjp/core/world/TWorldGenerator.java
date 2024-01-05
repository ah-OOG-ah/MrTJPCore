package mrtjp.core.world;

import mrtjp.core.math.MathLib;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Random;
import java.util.Set;

public abstract class TWorldGenerator extends WorldGenerator {

    // Really not sure why this was in there
    //@Override
    //public abstract boolean generate(World w, Random rand, int x, int y, int z);

    protected boolean canSetBlock(
        World w,
        int x,
        int y,
        int z,
        Set<Pair<Block, Integer>> material
        ) {
        if (material.isEmpty()) return true;
        final Block block = w.getBlock(x, y, z);
        return material.stream().anyMatch(pair ->
            (pair.getRight() == -1 || pair.getRight() == w.getBlockMetadata(x, y, z)) &&
            (block.isReplaceableOreGen(w, x, y, z, pair.getLeft()) || block
                .isAssociatedBlock(pair.getLeft()))
        );
    }

    protected Boolean setBlock(
        World w,
        int x,
        int y,
        int z,
        Set<Pair<Pair<Block, Integer>, Integer>> cluster,
        Set<Pair<Block, Integer>> material
    ) {
        if (canSetBlock(w, x, y, z, material)) {
            final Pair<Block, Integer> genBlock = MathLib.weightedRandom(cluster, w.rand);
            w.setBlock(x, y, z, genBlock.getLeft(), genBlock.getRight(), 2);
            return true;
        }
        return false;
    }

    protected boolean setBlock(
        World w,
        int x,
        int y,
        int z,
        Pair<Block, Integer> cluster,
        Set<Pair<Block, Integer>> material
    ) {
        if (canSetBlock(w, x, y, z, material)) {
            w.setBlock(x, y, z, cluster.getLeft(), cluster.getRight(), 2);
            return true;
        }
        return false;
    }
}
