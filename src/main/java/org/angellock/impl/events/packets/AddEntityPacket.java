package org.angellock.impl.events.packets;

import org.angellock.impl.events.AbstractEventProcessor;
import org.angellock.impl.events.types.EntityEmergedEvent;
import org.angellock.impl.util.math.Position;
import org.geysermc.mcprotocollib.network.packet.Packet;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.spawn.ClientboundAddEntityPacket;

public class AddEntityPacket extends AbstractEventProcessor<ClientboundAddEntityPacket> {
    public AddEntityPacket() {
        this.preAction = ((packet) -> {
            Position position = new Position(packet.getX(), packet.getY(), packet.getZ());
            EntityEmergedEvent entityEmergedEvent = new EntityEmergedEvent(packet.getType(), position);
            dispatch(entityEmergedEvent);
        });
    }

    @Override
    protected boolean isTargetPacket(Packet minecraftPacket) {
        return (minecraftPacket instanceof ClientboundAddEntityPacket);
    }
}
