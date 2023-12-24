package mrtjp.core.block;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.packet.ICustomPacketTile;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import mrtjp.core.handler.MrTJPCoreSPH;
import mrtjp.core.world.WorldLib;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class InstancedBlockTile extends TileEntity implements ICustomPacketTile {

    protected long schedTick = -1L;

    public void prepair(int meta) {}

    public void onBlockPlaced(
        int side,
        int meta,
        EntityPlayer player,
        ItemStack stack,
        Vector3 hit
    ) {}

    public void onBlockRemoval() {}

    public void onNeighborChange(Block b) {}

    public boolean canConnectRS() {
        return false;
    }
    public int strongPower(int side) {
        return 0;
    }
    public int weakPower(int side) {
        return strongPower(side);
    }

    public int getLightValue() {
        return  0;
    }

    public boolean isFireSource(int side) {
        return false;
    }

    public boolean isSolid(int side) {
        return true;
    }

    public boolean onBlockActivated(EntityPlayer player, int side) {
        return false;
    }

    public boolean onBlockClicked(EntityPlayer player) {
        return false;
    }

    public void onEntityCollision(Entity ent) {}

    public Cuboid6 getBlockBounds() {
        return Cuboid6.full;
    }

    public Cuboid6 getCollisionBounds() {
        return Cuboid6.full;
    }

    public void onScheduledTick() {}

    public void updateClient() {}

    public void update() {}

    public void randomTick(Random rand) {}

    public abstract Block getBlock();

    public int getMetaData() {
        return getBlockMetadata();
    }

    public ItemStack getPickBlock() {
        return new ItemStack(getBlock(), 1, getMetaData());
    }

    public List<ItemStack> addHarvestContents(List<ItemStack> ist) {
        ist.add(getPickBlock());
        return ist;
    }

    public World world = worldObj;
    public int x = xCoord;
    public int y = yCoord;
    public int z = zCoord;

    public void scheduleTick(int time) {
        long tn = world.getTotalWorldTime() + time;
        if (schedTick > 0L && schedTick < tn) return;
        schedTick = tn;
        markDirty();
    }

    public boolean isTickScheduled() {
        return schedTick >= 0L;
    }

    public void breakBlock_do() {
        List<ItemStack> il = new ArrayList<>();
        addHarvestContents(il);
        for (ItemStack stack : il) WorldLib.dropItem(world, x, y, z, stack);
        world.setBlockToAir(x, y, z);
    }

    @Override
    public void markDirty() {
        world.markTileEntityChunkModified(x, y, z, this);
    }

    final public void markRender() {
        world.func_147479_m(x, y, z);
    }

    final public void markLight() {
        world.func_147451_t(x, y, z);
    }

    final public void markDescUpdate() {
        world.markBlockForUpdate(x, y, z);
    }

    @Override
    final public void updateEntity() {
        if (world.isRemote) {
            updateClient();
            return;
        } else update();
        if (schedTick < 0L) return;
        long time = world.getTotalWorldTime();
        if (schedTick <= time) {
            schedTick = -1L;
            onScheduledTick();
            markDirty();
        }
    }

    @Override
    final public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        schedTick = tag.getLong("sched");
        load(tag);
    }

    @Override
    final public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setLong("sched", schedTick);
        save(tag);
    }

    public void save(NBTTagCompound tag) {}
    public void load(NBTTagCompound tag) {}

    @Override
    final public FMLProxyPacket getDescriptionPacket() {
        PacketCustom packet = writeStream(0);
        writeDesc(packet);
        if (compressDesc()) packet.compress();
        return packet.toPacket();
    }

    public boolean compressDesc() {
        return false;
    }

    final public void handleDescriptionPacket(PacketCustom packet) {
        int ub = packet.readUByte();
        if (ub == 0) {
            readDesc(packet);
        }
        read(packet, ub);
    }

    public void read(MCDataInput in, int key) {}

    public void readDesc(MCDataInput in) {}
    public void writeDesc(MCDataOutput out) {}

    final public PacketCustom writeStream(int key) {

        PacketCustom stream = new PacketCustom(MrTJPCoreSPH.channel, MrTJPCoreSPH.instance.tilePacket);
        stream.writeCoord(x, y, z).writeByte(key);
        return stream;
    }

    public StreamSender streamToSend(PacketCustom out) {
        return new StreamSender(this, out);
    }
    public PacketCustom sendToStream(StreamSender send) {
        return send.out;
    }

    // This used to be a case class
    public class StreamSender {

        public final PacketCustom out;
        public final InstancedBlockTile parent;

        public StreamSender(InstancedBlockTile parent, PacketCustom out) {
            this.out = out;
            this.parent = parent;
        }

        public void sendToChunk() {
            out.sendToChunk(parent.world, parent.x >> 4, parent.z >> 4);
        }
    }
}
