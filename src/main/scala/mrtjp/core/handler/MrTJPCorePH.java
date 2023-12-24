package mrtjp.core.handler;

import codechicken.lib.packet.ICustomPacketTile;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.vec.BlockCoord;
import mrtjp.core.world.WorldLib;
import net.minecraft.world.World;

public class MrTJPCorePH {

    public static final MrTJPCorePH instance = new MrTJPCorePH();

    public static final String channel =  MrTJPCoreMod.modName;
    public static final int tilePacket = 1;
    public static final int messagePacket = 2;
    public static final int guiPacket = 3;
    public static final int keyBindPacket = 4;

    public static void handleTilePacket(World world, PacketCustom packet, BlockCoord pos) {
        ICustomPacketTile t = WorldLib.getTileEntity(world, pos, ICustomPacketTile.class);
        if (t != null) t.handleDescriptionPacket(packet);
    }
}
