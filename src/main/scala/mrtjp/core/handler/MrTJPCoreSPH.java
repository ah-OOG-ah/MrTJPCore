package mrtjp.core.handler;

import codechicken.lib.packet.PacketCustom;
import mrtjp.core.data.KeyTracking;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.INetHandlerPlayServer;

public class MrTJPCoreSPH extends MrTJPCorePH implements PacketCustom.IServerPacketHandler {

    public static final MrTJPCoreSPH instance = new MrTJPCoreSPH();

    public static final String channel =  MrTJPCoreMod.modName;
    public static final int messagePacket = 2;
    public static final int guiPacket = 3;
    public static final int keyBindPacket = 4;

    @Override
    public void handlePacket(
        PacketCustom packet,
        EntityPlayerMP sender,
        INetHandlerPlayServer nethandler
    ) {
        int type = packet.getType();
        if (type == tilePacket) {
            handleTilePacket(sender.theItemInWorldManager.theWorld, packet, packet.readCoord());
        } else if (type == keyBindPacket) {
            KeyTracking.updatePlayerKey(packet.readUByte(), sender, packet.readBoolean());
        }
    }
}
