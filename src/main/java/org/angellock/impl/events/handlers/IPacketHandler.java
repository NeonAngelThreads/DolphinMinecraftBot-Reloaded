package org.angellock.impl.events.handlers;

import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundLoginPacket;
@FunctionalInterface
public interface IPacketHandler<T> {
    void handle(T packet);
}
