package org.angellock.impl.events;

import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.network.event.session.SessionAdapter;
import org.geysermc.mcprotocollib.network.event.session.SessionListener;
import org.geysermc.mcprotocollib.network.packet.Packet;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftPacket;

public abstract class IMinecraftSessionListener extends SessionAdapter {

    @Override
    public void packetReceived(Session session, Packet packet) {
        if (packet instanceof MinecraftPacket){
            this.onPacket(session, (MinecraftPacket) packet);
        }
    }
    public abstract void onPacket(Session session, MinecraftPacket packet);
}
