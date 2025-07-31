package org.angellock.impl.events.packets;

import org.angellock.impl.events.AbstractEventProcessor;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundPlayerInfoRemovePacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundPlayerInfoUpdatePacket;

public class PlayerLogInfo {
    public static class RemoveHandler extends AbstractEventProcessor<ClientboundPlayerInfoRemovePacket> {
        @Override
        protected boolean isTargetPacket(MinecraftPacket packet) {
            return (packet instanceof ClientboundPlayerInfoRemovePacket);
        }
    }

    public static class UpdateHandler extends AbstractEventProcessor<ClientboundPlayerInfoUpdatePacket> {
        @Override
        protected boolean isTargetPacket(MinecraftPacket packet) {
            return (packet instanceof ClientboundPlayerInfoUpdatePacket);
        }
    }
}
