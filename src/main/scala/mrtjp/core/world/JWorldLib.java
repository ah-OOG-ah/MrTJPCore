package mrtjp.core.world;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class JWorldLib {

    public static Pair<Block, Integer> getBlockMetaPair(World world, int x, int y, int z) {
        return  new ImmutablePair<>(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
    }
}
