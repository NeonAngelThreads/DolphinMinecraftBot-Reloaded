package org.angellock.impl.events;

import org.angellock.impl.events.bukkit.ActiveListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class HandlerMapper {
    private final EnumMap<EventPriority, CopyOnWriteArrayList<ActiveListener>> handlers = new EnumMap<>(EventPriority.class);

    public HandlerMapper() {
        for (EventPriority p : EventPriority.values()) {
            handlers.put(p, new CopyOnWriteArrayList<>());
        }
    }

    void register(ActiveListener rl) {
        handlers.get(rl.getPriority()).add(rl);
    }

    void unregister(ActiveListener rl) {
        handlers.get(rl.getPriority()).remove(rl);
    }

    public List<ActiveListener> getRegisteredListenersInOrder() {
        List<ActiveListener> all = new ArrayList<>();
        for (EventPriority p : EventPriority.values()) {
            all.addAll(handlers.get(p));
        }
        return Collections.unmodifiableList(all);
    }
}
