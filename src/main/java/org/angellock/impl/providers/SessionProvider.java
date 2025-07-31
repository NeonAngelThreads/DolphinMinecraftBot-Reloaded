package org.angellock.impl.providers;

import org.geysermc.mcprotocollib.network.Session;

public interface SessionProvider {
    Session getSession();
}
