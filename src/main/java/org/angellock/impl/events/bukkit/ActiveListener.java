package org.angellock.impl.events.bukkit;

import org.angellock.impl.events.EventPriority;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ActiveListener {
    private final Method action;
    private final Class<?> eventType;
    private EventPriority priority = EventPriority.NORMAL;

    public ActiveListener(Method action, Class<?> eventType) {
        action.setAccessible(true);
        this.action = action;
        this.eventType = eventType;
    }

    public ActiveListener(Method action, Class<?> eventType, EventPriority priority) {
        this(action, eventType);
        this.priority = priority;
    }

    public EventPriority getPriority() {
        return priority;
    }

    public void call(Event event) throws InvocationTargetException, IllegalAccessException {
        if (this.eventType.isInstance(event)) {
            this.action.invoke(event);
        }
    }
}
