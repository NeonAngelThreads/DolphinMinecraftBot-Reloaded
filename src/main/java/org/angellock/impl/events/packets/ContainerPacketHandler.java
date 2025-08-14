package org.angellock.impl.events.packets;

import org.angellock.impl.events.AbstractEventProcessor;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundPlayerChatPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.inventory.ClientboundContainerSetContentPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.inventory.ClientboundContainerSetSlotPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.inventory.ClientboundOpenScreenPacket;

public class ContainerPacketHandler extends AbstractEventProcessor<ClientboundOpenScreenPacket> {
    @Override
    protected boolean isTargetPacket(MinecraftPacket packet) {
        return (packet instanceof ClientboundOpenScreenPacket);
    }
}
