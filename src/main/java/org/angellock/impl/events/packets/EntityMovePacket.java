package org.angellock.impl.events.packets;

import org.angellock.impl.events.AbstractEventProcessor;
import org.geysermc.mcprotocollib.network.packet.Packet;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundMoveEntityPosPacket;

public class EntityMovePacket extends AbstractEventProcessor<ClientboundMoveEntityPosPacket> {
    public EntityMovePacket() {
        this.preAction = (packet) -> {
            return;
        };
    }

    @Override
    protected boolean isTargetPacket(Packet minecraftPacket) {
        return (minecraftPacket instanceof ClientboundMoveEntityPosPacket);
    }
}
