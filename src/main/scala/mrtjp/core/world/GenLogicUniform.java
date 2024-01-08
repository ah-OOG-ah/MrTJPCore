package mrtjp.core.world;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class GenLogicUniform extends TGenerationLogic {

    public WorldGenerator gen = null;

    public int attempts = 1;
    public int minY = 0;
    public int maxY = 0;

    @Override
    public boolean generate_impl(
        World w,
        int chunkX,
        int chunkZ,
        Random rand
    ) {
        boolean generated = false;
        for (int i = 0; i < attempts; ++i) {
            final int x = chunkX * 16 + rand.nextInt(16);
            final int y = minY + rand.nextInt(maxY - minY);
            final int z = chunkZ * 16 + rand.nextInt(16);
            if (postFiltCheck(w, x, z, rand))
                generated |= gen.generate(w, rand, x, y, z);
        }
        return generated;
    }
}
