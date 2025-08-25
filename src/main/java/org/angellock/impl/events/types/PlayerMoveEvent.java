package org.angellock.impl.events.types;

import org.angellock.impl.events.HandlerMapper;
import org.angellock.impl.events.bukkit.Event;
import org.angellock.impl.ingame.Player;
import org.angellock.impl.util.math.Position;

public class PlayerMoveEvent extends Event {
    private static final HandlerMapper HANDLERS = new HandlerMapper();
    private final Player targetPlayer;
    private final Position movePosition;

    public PlayerMoveEvent(Player targetPlayer, Position movePosition) {
        this.targetPlayer = targetPlayer;
        this.movePosition = movePosition;
    }

    public Player getPlayer() {
        return targetPlayer;
    }

    public Position getMovePosition() {
        return movePosition;
    }

    public static HandlerMapper getHandlers() {
        return HANDLERS;
    }

    @Override
    public HandlerMapper getMapper() {
        return HANDLERS;
    }
}
