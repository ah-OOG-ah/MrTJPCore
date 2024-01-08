package mrtjp.core.world;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraftforge.common.BiomeDictionary;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public abstract class TGenerationLogic implements ISimpleStructureGen {

    public String name = "";

    public boolean dimensionBlacklist = true;
    public Set<Integer> dimensions = new HashSet<>();
    {
        dimensions.add(-1);
        dimensions.add(1);
    }

    public boolean biomeBlacklist = true;
    public Set<Set<BiomeDictionary.Type>> biomes = new HashSet<>();

    public boolean typeBlacklist = true;
    public Set<WorldType> types = new HashSet<>(Collections.singleton(WorldType.FLAT));

    public int resistance = 0;
    public boolean allowRetroGen = false;

    @Override
    public String genID() {
        return name;
    }

    public boolean preFiltCheck(
        World w,
        int chunkX,
        int chunkZ,
        Random rand,
        boolean isRetro
    ) {
        if (isRetro && !allowRetroGen) return false;
        if (dimensionBlacklist == dimensions.contains(w.provider.dimensionId)) return false;
        if (typeBlacklist == types.contains(w.provider.terrainType)) return false;
        if (resistance > 1 && rand.nextInt(resistance) != 0) return false;

        return true;
    }

    public boolean postFiltCheck(World w, int x, int z, Random rand) {
        final Set<BiomeDictionary.Type> types =
            new HashSet<>(Arrays.asList(BiomeDictionary.getTypesForBiome(w.getBiomeGenForCoords(x, z))));
        return biomeBlacklist != biomes.contains(types);
    }

    @Override
    public boolean generate(
        World w,
        int chunkX,
        int chunkZ,
        Random rand,
        boolean isRetro
    ) {
        if (!preFiltCheck(w, chunkX, chunkZ, rand, isRetro)) return false;
        return generate_impl(w, chunkX, chunkZ, rand);
    }

    public abstract boolean generate_impl(World w, int chunkX, int chunkZ, Random rand);
}
