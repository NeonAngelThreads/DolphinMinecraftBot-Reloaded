package org.angellock.impl.events;

import org.geysermc.mcprotocollib.network.event.session.ConnectedEvent;

public interface IConnectListener extends ISessionAdaptable{

    @Override
    default void connected(ConnectedEvent event){
        onConnected(event);
    }

    void onConnected(ConnectedEvent event);
}
