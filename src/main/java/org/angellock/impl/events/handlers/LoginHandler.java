package org.angellock.impl.events.handlers;

import org.angellock.impl.events.AbstractEventProcessor;
import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.network.packet.Packet;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundLoginPacket;

public class LoginHandler extends AbstractEventProcessor<ClientboundLoginPacket> {
    @Override
    protected boolean isTargetPacket(Packet packet) {
        return (packet instanceof ClientboundLoginPacket);
    }
}
