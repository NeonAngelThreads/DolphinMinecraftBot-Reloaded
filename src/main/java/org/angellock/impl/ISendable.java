package org.angellock.impl;

import org.geysermc.mcprotocollib.protocol.codec.MinecraftPacket;

public interface ISendable {
    void sendPacket(MinecraftPacket packet);
}
