package mrtjp.core.block;

import codechicken.lib.vec.Cuboid6;
import net.minecraft.block.Block;

public abstract class TPlantTile extends InstancedBlockTile {

    @Override
    public Cuboid6 getCollisionBounds() {
        return null;
    }

    public boolean canBlockStay() {
        Block p = world().getBlock(xCoord, yCoord, zCoord);
        if (p instanceof TPlantBlock) {
            return ((TPlantBlock) p).initialCanStay(world(), xCoord, yCoord, zCoord);
        }
        return false;
    }

    public boolean applyBonemeal() {
        return false;
    }

    @Override
    public void onNeighborChange(Block b) {
        super.onNeighborChange(b);
        dropIfCantStay();
    }

    public boolean dropIfCantStay() {
        if (!canBlockStay()) {
            breakBlock_do();
            return true;
        }
        return false;
    }

    @Override
    public boolean isSolid(int side) {
        return false;
    }
}
