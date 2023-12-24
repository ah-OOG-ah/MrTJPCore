package mrtjp.core.block;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public interface TInstancedBlockRender {

    void renderWorldBlock(
        RenderBlocks r,
        IBlockAccess w,
        int x,
        int y,
        int z,
        int meta
    );

    void renderInvBlock(RenderBlocks r, int meta);

    default void renderBreaking(IBlockAccess w, int x, int y, int z, IIcon icon) {}

    default void randomDisplayTick(World w, int x, int y, int z, Random r) {}

    void registerIcons(IIconRegister reg);

    IIcon getIcon(int side, int meta);
}
