package mrtjp.core.world;

import net.minecraft.world.World;

import java.util.Random;

public interface ISimpleStructureGen {

    String genID();

    boolean generate(
        World w,
        int chunkX,
        int chunkZ,
        Random rand,
        boolean isRetro
    );
}
