package mrtjp.core.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mrtjp.core.world.WorldLib;

/**
 * InstancedBlocks implement ITileEntityProvider, but may not label themselves as TEs
 */
public class InstancedBlock extends BlockContainer {

    // Upon creation, this block registers itself... I feel like that could be an issue
    public InstancedBlock(String name, Material mat) {
        super(mat);
        setBlockName(name);
        GameRegistry.registerBlock(this, getItemBlockClass(), name);
    }

    private boolean singleTile = false;
    private final Map<Integer, Class<? extends InstancedBlockTile>> tiles = new HashMap<>(16);


    public Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockCore.class;
    }

    /**
     * Registers the given class as a TE with the registry, and stores it as a tile of this block with the given meta.
     */
    public void addTile(Class<? extends InstancedBlockTile> t, int meta) {
        // Not sure if this branch is ever used
        tiles.put(meta, t);
        GameRegistry.registerTileEntity(t, getUnlocalizedName() + "|" + meta);
    }

    /**
     * If you only want to use one meta, use this
     */
    public void addSingleTile(Class<? extends InstancedBlockTile> t) {
        addTile(t, 0);
        singleTile = true;
    }

    /**
     * Originally, MrTJPCore only looked for any meta with a TE, not a TE with the given meta. For now, I leave it be.
     */
    @Override
    public boolean hasTileEntity(int metadata) {
        return !tiles.isEmpty();
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int damageDropped(int damage) {
        return damage;
    }

    @Override
    public void harvestBlock(World w, EntityPlayer player, int x, int y, int z, int l) {}

    @Override
    public int getRenderType() {
        return TileRenderRegistry.renderID();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister reg) {
        TileRenderRegistry.registerIcons(this, reg);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return TileRenderRegistry.getIcon(this, side, meta);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World w, int x, int y, int z, Random rand) {
        int md = w.getBlockMetadata(x, y, z);
        TInstancedBlockRender r = TileRenderRegistry.getRenderer(this, md);
        if (r != null) r.randomDisplayTick(w, x, y, z, rand);
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2) {
        return null;
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        InstancedBlockTile t = null;
        try {
            t = (singleTile) ? tiles.get(0).newInstance() : tiles.get(meta).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (t != null) t.prepair(meta);
        return t;
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
        if (world.isRemote) return true;
        else {
            Block b = world.getBlock(x, y, z);
            int md = world.getBlockMetadata(x, y, z);
            if (b.canHarvestBlock(player, md) && !player.capabilities.isCreativeMode) {
                List<ItemStack> il = getDrops(world, x, y, z, md, EnchantmentHelper.getFortuneModifier(player));
                for (ItemStack it : il) WorldLib.dropItem(world, x, y, z, it);
            }
            world.setBlockToAir(x, y, z);
            return true;
        }
    }

    @Override
    public ArrayList<ItemStack> getDrops(World w, int x, int y, int z, int meta, int fortune) {
        ArrayList<ItemStack> list = new ArrayList<>();
        TileEntity t = w.getTileEntity(x, y, z);
        if (t instanceof InstancedBlockTile) {
            ((InstancedBlockTile) t).addHarvestContents(list);
        }
        return list;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World w, int x, int y, int z, EntityPlayer p) {
        TileEntity te = w.getTileEntity(x, y, z);
        if (te instanceof InstancedBlockTile) {
            return ((InstancedBlockTile) te).getPickBlock();
        }
        return super.getPickBlock(target, w, x, y, z, p);
    }

    @Override
    public void onNeighborBlockChange(World w, int x, int y, int z, Block b) {
        TileEntity te = w.getTileEntity(x, y, z);
        if (te instanceof InstancedBlockTile) {
            ((InstancedBlockTile) te).onNeighborChange(b);
        }
    }

    public void postBlockSetup(World w, int x, int y, int z, int side, int meta, EntityPlayer player, ItemStack stack,
            Vector3 hit) {
        TileEntity te = w.getTileEntity(x, y, z);
        if (te instanceof InstancedBlockTile) {
            ((InstancedBlockTile) te).onBlockPlaced(side, meta, player, stack, hit);
        }
    }

    @Override
    public void breakBlock(World w, int x, int y, int z, Block b, int meta) {
        TileEntity te = w.getTileEntity(x, y, z);
        if (te instanceof InstancedBlockTile) {
            ((InstancedBlockTile) te).onBlockRemoval();;
        }
        super.breakBlock(w, x, y, z, b, meta);
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess w, int x, int y, int z, int side) {
        TileEntity te = w.getTileEntity(x, y, z);
        if (te instanceof InstancedBlockTile) {
            return ((InstancedBlockTile) te).canConnectRS();
        }
        return super.canConnectRedstone(w, x, y, z, side);
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess w, int x, int y, int z, int side) {
        TileEntity te = w.getTileEntity(x, y, z);
        if (te instanceof InstancedBlockTile) {
            return ((InstancedBlockTile) te).strongPower(side);
        }
        return 0;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess w, int x, int y, int z, int side) {
        TileEntity te = w.getTileEntity(x, y, z);
        if (te instanceof InstancedBlockTile) {
            return ((InstancedBlockTile) te).weakPower(side);
        }
        return 0;
    }

    @Override
    public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer player, int side, float hx, float hy,
            float hz) {
        TileEntity te = w.getTileEntity(x, y, z);
        if (te instanceof InstancedBlockTile) {
            ((InstancedBlockTile) te).onBlockActivated(player, side);
        }
        return false;
    }

    @Override
    public void onBlockClicked(World w, int x, int y, int z, EntityPlayer player) {
        TileEntity te = w.getTileEntity(x, y, z);
        if (te instanceof InstancedBlockTile) {
            ((InstancedBlockTile) te).onBlockClicked(player);
        }
        super.onBlockClicked(w, x, y, z, player);
    }

    @Override
    public void onEntityCollidedWithBlock(World w, int x, int y, int z, Entity ent) {
        TileEntity te = w.getTileEntity(x, y, z);
        if (te instanceof InstancedBlockTile) {
            ((InstancedBlockTile) te).onEntityCollision(ent);
        }
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess w, int x, int y, int z) {
        TileEntity te = w.getTileEntity(x, y, z);
        if (te instanceof InstancedBlockTile) {
            ((InstancedBlockTile) te).getBlockBounds().setBlockBounds(this);
        }
        super.setBlockBoundsBasedOnState(w, x, y, z);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World w, int x, int y, int z) {
        TileEntity te = w.getTileEntity(x, y, z);
        if (te instanceof InstancedBlockTile) {
            Cuboid6 box = ((InstancedBlockTile) te).getCollisionBounds();
            if (box != null) {
                return box.copy().add(new Vector3(x, y, z)).toAABB();
            }
            return null;
        }
        return super.getCollisionBoundingBoxFromPool(w, x, y, z);
    }

    @Override
    public int getLightValue(IBlockAccess w, int x, int y, int z) {
        TileEntity te = w.getTileEntity(x, y, z);
        if (te instanceof InstancedBlockTile) {
            return ((InstancedBlockTile) te).getLightValue();
        }
        return super.getLightValue(w, x, y, z);
    }

    @Override
    public boolean isFireSource(World w, int x, int y, int z, ForgeDirection side) {
        TileEntity te = w.getTileEntity(x, y, z);
        if (te instanceof InstancedBlockTile) {
            return ((InstancedBlockTile) te).isFireSource(side.ordinal());
        }
        return super.isFireSource(w, x, y, z, side);
    }

    @Override
    public boolean isSideSolid(IBlockAccess w, int x, int y, int z, ForgeDirection side) {
        TileEntity te = w.getTileEntity(x, y, z);
        if (te instanceof InstancedBlockTile) {
            return ((InstancedBlockTile) te).isSolid(side.ordinal());
        }
        return super.isSideSolid(w, x, y, z, side);
    }

    @Override
    public void updateTick(World w, int x, int y, int z, Random rand) {
        TileEntity te = w.getTileEntity(x, y, z);
        if (te instanceof InstancedBlockTile) {
            ((InstancedBlockTile) te).randomTick(rand);
        }
        super.updateTick(w, x, y, z, rand);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item thisItem, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(thisItem, 1, 0));
        for (int i : IntStream.range(1, tiles.size()).toArray()) {
            if (tiles.get(i) != null) list.add(new ItemStack(thisItem, 1, i));
        }
    }
}
