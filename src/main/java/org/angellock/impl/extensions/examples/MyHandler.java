package org.angellock.impl.extensions.examples;

import org.angellock.impl.events.AbstractEventProcessor;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundPlayerChatPacket;

public class MyHandler extends AbstractEventProcessor<ClientboundPlayerChatPacket> {
    @Override
    protected boolean isTargetPacket(MinecraftPacket packet) {
        return (packet instanceof ClientboundPlayerChatPacket);
    }
}
