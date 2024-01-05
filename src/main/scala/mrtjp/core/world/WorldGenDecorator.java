package mrtjp.core.world;

import mrtjp.core.math.MathLib;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class WorldGenDecorator extends TWorldGenerator {
    public Set<Pair<Pair<Block, Integer>, Integer>> cluster = new HashSet<>();
    public Set<Pair<Block, Integer>> material = new HashSet<>();
    public Set<Pair<Block, Integer>> soil = new HashSet<>();
    public int clusterSize = 1;

    public boolean checkStay = true;
    public boolean seeSky = false;
    public boolean underTree = false;

    public int stackHeight = 1;
    public int dx = 4;
    public int dy = 2;
    public int dz = 4;

    @Override
    public boolean generate(
        World w,
        Random rand,
        int xStart,
        int yStart,
        int zStart
    ) {
        boolean generated = false;
        for (int i = 0; i < cluster.size(); ++i) {

            final int x = xStart + rand.nextInt(dx) - rand.nextInt(dx);
            int y = yStart + 1 + ((dy > 1) ? rand.nextInt(dy) - rand.nextInt(dy) : 0);
            final int z = zStart + rand.nextInt(dz) - rand.nextInt(dz);

            if (checkLocation(w, x, y, z)) {
                final Pair<Block, Integer> p = MathLib.weightedRandom(cluster);
                final Block b = p.getLeft();
                final int m = p.getRight();
                final int h = (stackHeight > 1) ? rand.nextInt(stackHeight) : 0;

                for (int s = 0; s < h; ++s) {
                    if (!checkStay || b.canBlockStay(w, x, y, z))
                        generated |= w.setBlock(x, y, z, b, m, 2);
                    else
                        break;
                    ++y;
                    if (!canSetBlock(w, x, y, z, material)) break;
                }
            }
        }
        return generated;
    }

    public boolean checkLocation(World w, int x, int y, int z) {
        if (seeSky && !w.canBlockSeeTheSky(x, y, z)) return false;
        if (!canSetBlock(w, x, y, z, material)) return false;
        if (!canSetBlock(w, x, y - 1, z, soil)) return false;
        return true;
    }
}
