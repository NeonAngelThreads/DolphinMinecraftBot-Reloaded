package org.angellock.impl.events.packets;

import org.angellock.impl.events.AbstractEventProcessor;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.title.ClientboundSetTitleTextPacket;

public class TitlePacketHandler extends AbstractEventProcessor<ClientboundSetTitleTextPacket> {
    @Override
    protected boolean isTargetPacket(MinecraftPacket packet) {
        return (packet instanceof ClientboundSetTitleTextPacket);
    }
}
