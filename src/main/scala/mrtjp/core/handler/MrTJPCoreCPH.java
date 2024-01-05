package mrtjp.core.handler;

import codechicken.lib.packet.PacketCustom;
import mrtjp.core.gui.GuiHandler;
import mrtjp.core.world.Messenger;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

public class MrTJPCoreCPH extends MrTJPCorePH implements PacketCustom.IClientPacketHandler {

    public static final MrTJPCoreCPH instance = new MrTJPCoreCPH();

    public static final String channel =  MrTJPCoreMod.modName;

    public void handlePacket(PacketCustom packet, Minecraft mc, INetHandlerPlayClient nethandler) {
        final World world = mc.theWorld;
        switch (packet.getType()) {
            case tilePacket: handleTilePacket(world, packet, packet.readCoord()); break;
            case messagePacket:
                Messenger.addMessage(
                    packet.readDouble(),
                    packet.readDouble(),
                    packet.readDouble(),
                    packet.readString()
                ); break;
            case guiPacket: GuiHandler.receiveGuiPacket(packet); break;
            default:
        }
    }
}
