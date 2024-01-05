package mrtjp.core.world;

import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class WorldGenClusterizer extends TWorldGenerator {
    public Set<Pair<Pair<Block, Integer>, Integer>> cluster = new HashSet<>();
    public Set<Pair<Block, Integer>> material = new HashSet<>();
    public int clusterSize = 1;

    @Override
    public boolean generate(World w, Random rand, int x, int y, int z) {
        if (clusterSize < 4) return generateSmall(w, rand, x, y, z);
        return generateNormal(w, rand, x, y, z);
    }

    public boolean generateSmall(World w, Random rand, int x, int y, int z) {
        boolean generated = false;
        for (int i = 0; i < clusterSize; ++i) {
            final int dx = x + rand.nextInt(2);
            final int dy = y + rand.nextInt(2);
            final int dz = z + rand.nextInt(2);
            generated |= setBlock(w, dx, dy, dz, cluster, material);
        }
        return generated;
    }

    public boolean generateNormal(
        World w,
        Random rand,
        int x,
        int y,
        int z
    ) {
        final float f = (float) (rand.nextFloat() * Math.PI);
        final float xNDir = (float) (x + 8 + (Math.sin(f) * clusterSize) / 8f);
        final float xPDir = (float) (x + 8 - (Math.sin(f) * clusterSize) / 8f);
        final float zNDir = (float) (z + 8 + (Math.cos(f) * clusterSize) / 8f);
        final float zPDir = (float) (z + 8 - (Math.cos(f) * clusterSize) / 8f);
        final int yNDir = (y + rand.nextInt(3)) - 2;
        final int yPDir = (y + rand.nextInt(3)) - 2;

        final float dx = xPDir - xNDir;
        final float dy = yPDir - yNDir;
        final float dz = zPDir - zNDir;

        boolean generated = false;
        for (int i = 0; i < clusterSize; ++i) {
            final float xCenter = xNDir + (dx * i) / clusterSize;
            final float yCenter = yNDir + (dy * i) / clusterSize;
            final float zCenter = zNDir + (dz * i) / clusterSize;

            // I haven't the faintest idea why MrTJP did this. Just use nextFloat dangit!
            // I'm leaving it like this in the hopes of having identical worldgen
            final float size = ((((float) rand.nextDouble()) * clusterSize) / 16f);

            final float hMod = (float) (((Math.sin(
                            (i * Math.PI) / clusterSize
                        ) + 1f) * size + 1f) * 0.5f);
            final float vMod = (float) (((Math.sin(
                            (i * Math.PI) / clusterSize
                        ) + 1f) * size + 1f) * 0.5f);

            final int x0 = MathHelper.floor_float(xCenter - hMod);
            final int y0 = MathHelper.floor_float(yCenter - vMod);
            final int z0 = MathHelper.floor_float(zCenter - hMod);

            final int x1 = MathHelper.floor_float(xCenter + hMod);
            final int y1 = MathHelper.floor_float(yCenter + vMod);
            final int z1 = MathHelper.floor_float(zCenter + hMod);

            for (int blockX = x0; blockX <= x1; ++blockX) {
                float xDistSq = ((blockX + 0.5f) - xCenter) / hMod;
                xDistSq *= xDistSq;
                if (xDistSq < 1f) {
                    for (int blockY = y0; blockY <= y1; ++blockY) {
                        float yDistSq = ((blockY + 0.5f) - yCenter) / vMod;
                        yDistSq *= yDistSq;
                        final float xyDistSq = yDistSq + xDistSq;
                        if (xyDistSq < 1f) {
                            for (int blockZ = z0; blockZ <= z1; ++blockZ) {
                                float zDistSq = ((blockZ + 0.5f) - zCenter) / hMod;
                                zDistSq *= zDistSq;
                                if (zDistSq + xyDistSq < 1f)
                                    generated |= setBlock(
                                        w,
                                        blockX,
                                        blockY,
                                        blockZ,
                                        cluster,
                                        material
                                    );
                            }
                        }
                    }
                }
            }
        }
        return generated;
    }
}
