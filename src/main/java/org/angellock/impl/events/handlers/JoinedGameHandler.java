package org.angellock.impl.events.handlers;

import org.angellock.impl.events.AbstractEventProcessor;
import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftPacket;
import org.geysermc.mcprotocollib.protocol.packet.login.serverbound.ServerboundLoginAcknowledgedPacket;

public class JoinedGameHandler extends AbstractEventProcessor<ServerboundLoginAcknowledgedPacket> {
    @Override
    protected boolean isTargetPacket(MinecraftPacket packet) {
        return (packet instanceof ServerboundLoginAcknowledgedPacket);
    }
}
