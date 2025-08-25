package org.angellock.impl.events;

@FunctionalInterface
public interface IPacketHandler<T> {
    void handle(T packet);
}
