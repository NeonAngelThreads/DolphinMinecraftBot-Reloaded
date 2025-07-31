package org.angellock.impl.events;

import org.geysermc.mcprotocollib.network.event.session.DisconnectedEvent;

public interface IDisconnectListener extends ISessionAdaptable {
    @Override
    default void disconnected(DisconnectedEvent event){
        onDisconnect(event);
    }

    void onDisconnect(DisconnectedEvent event);
}
