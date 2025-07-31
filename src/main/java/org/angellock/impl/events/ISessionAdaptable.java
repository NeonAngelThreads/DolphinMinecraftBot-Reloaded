package org.angellock.impl.events;

import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.network.event.session.*;
import org.geysermc.mcprotocollib.network.packet.Packet;

public interface ISessionAdaptable extends SessionListener {
    default void packetReceived(Session session, Packet packet){}

    default void packetSending(PacketSendingEvent event){}

    default void packetSent(Session session, Packet packet){}

    default void packetError(PacketErrorEvent event){}

    default void connected(ConnectedEvent event){}
    default void disconnecting(DisconnectingEvent event){}

    default void disconnected(DisconnectedEvent event){}
}
