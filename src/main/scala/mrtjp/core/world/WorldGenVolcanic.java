package mrtjp.core.world;

import mrtjp.core.math.MathLib;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class WorldGenVolcanic extends TWorldGenerator {
    public Set<Pair<Pair<Block, Integer>, Integer>> ashCluster = new HashSet<>();
    public Set<Pair<Pair<Block, Integer>, Integer>> conduitCluster = new HashSet<>();
    public Pair<Block, Integer> liq = null;
    public Set<Pair<Block, Integer>> material = new HashSet<>();
    public Set<Pair<Block, Integer>> materialStart = new HashSet<>();

    public int sizeMin = 32000;
    public int sizeMax = 64000;

    private final LinkedList<Triple<Integer, Integer, Integer>> stack = new LinkedList<>();
    private final Map<Pair<Integer, Integer>, Integer> test = new HashMap<>();

    @Override
    public boolean generate(
        World w,
        Random rand,
        int x,
        int y,
        int z
    ) {
        if (!canSetBlock(w, x, y, z, materialStart)) return false;
        stack.clear();
        test.clear();

        final int swh = WorldLib.findSurfaceHeight(w, x, z);
        int n = swh;

        for (int i = y; i < swh; ++i) {
            setBlock(w, x, i, z, liq, material);
            setBlock(w, x - 1, i, z, conduitCluster, material);
            setBlock(w, x + 1, i, z, conduitCluster, material);
            setBlock(w, x, i, z - 1, conduitCluster, material);
            setBlock(w, x, i, z + 1, conduitCluster, material);
        }

        final int head = 3 + rand.nextInt(4);
        final int spread = rand.nextInt(3);

        // was an until
        int size = MathLib.randomFromIntRange(sizeMin, sizeMax - 1, rand);

        scalaBreak: while (size > 0) {
            while (stack.isEmpty()) {
                setBlock(w, x, n, z, liq, material);
                test.clear();
                enqueueBlocks(x, n, z, head, rand);
                ++n;
                if (n > 125) break scalaBreak;
            }

            Triple<Integer, Integer, Integer> tmp = stack.pop();
            final int i = tmp.getLeft();
            final int j = tmp.getMiddle();
            final int k = tmp.getRight();
            w.getBlock(i, 64, k); // force chunk generation
            Integer pow = test.get(new ImmutablePair<>(i, k));
            if (
                w.getChunkProvider().chunkExists(i >> 4, k >> 4) && pow != null
            ) {
                int hm = w.getHeightValue(i, k) + 1;
                while (hm > 0 && isUnimportant(w, i, hm - 1, k)) --hm;

                if (hm <= j) {
                    if (isUnimportant(w, i, hm, k)) {
                        purgeArea(w, i, hm, k);
                        setBlock(w, i, hm, k, ashCluster, material);
                        if (j > hm) pow = Math.max(pow, spread);
                        enqueueBlocks(i, hm, k, pow, rand);
                        --size;
                    }
                }
            }
        }

        setBlock(w, x, n, z, liq, material);
        while (n >= swh && liq.getLeft() == w.getBlock(x, n, z)) {
            w.markBlockForUpdate(x, n, z);
            w.notifyBlocksOfNeighborChange(x, n, z, liq.getLeft());
            w.notifyBlockOfNeighborChange(x, n, z, liq.getLeft());
            w.scheduledUpdatesAreImmediate = true;
            liq.getLeft().updateTick(w, x, n, z, w.rand);
            w.scheduledUpdatesAreImmediate = false;
            n -= 1;
        }
        return true;
    }

    private void purgeArea(World w, int x, int y, int z) {
        if (w.isAirBlock(x, y, z)) return;
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                final Block b = w.getBlock(x + i, y, z + j);
                if ((b == Blocks.snow) || WorldLib.isAssociatedTreeBlock(w, x + i, y, z + j, b))
                    w.setBlockToAir(x + i, y, z + j);
            }
        }
        purgeArea(w, x, y + 1, z);
    }

    private void enq(int x, int y, int z, int p) {
        if (p > 0) {
            final int o = test.getOrDefault(new ImmutablePair<>(x, z), -1);
            if (p > o) {
                stack.push(new ImmutableTriple<>(x, y, z));
                test.put(new ImmutablePair<>(x, z), p);
            }
        }
    }

    private void enqueueBlocks(int x, int y, int z, int p, Random rand) {
        final int seed = rand.nextInt(16);
        enq(x - 1, y, z, (seed & 1) != 0 ? p - 1 : p);
        enq(x + 1, y, z, (seed & 2) != 0 ? p - 1 : p);
        enq(x, y, z - 1, (seed & 4) != 0 ? p - 1 : p);
        enq(x, y, z + 1, (seed & 8) != 0 ? p - 1 : p);
    }

    private boolean isUnimportant(World w, int x, int y, int z) {
        final Block b = w.getBlock(x, y, z);
        if (
            WorldLib.isBlockSoft(w, x, y, z, b) || WorldLib.isAssociatedTreeBlock(
                w,
                x,
                y,
                z,
                b
            )
        ) return true;
        if (
            b == Blocks.flowing_water || b == Blocks.water || b == Blocks.snow || b == Blocks.ice
        ) return true;
        return false;
    }
}
