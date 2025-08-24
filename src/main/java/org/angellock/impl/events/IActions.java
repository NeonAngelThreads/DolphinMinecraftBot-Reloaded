package org.angellock.impl.events;
@FunctionalInterface
public interface IActions <T>{
    void onAction(T packet);
}
