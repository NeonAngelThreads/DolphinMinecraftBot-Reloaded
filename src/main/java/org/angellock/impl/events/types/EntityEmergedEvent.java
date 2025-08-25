package org.angellock.impl.events.types;

import org.angellock.impl.events.HandlerMapper;
import org.angellock.impl.events.bukkit.Event;
import org.angellock.impl.util.math.Position;
import org.geysermc.mcprotocollib.protocol.data.game.entity.type.EntityType;

public class EntityEmergedEvent extends Event {
    private static final HandlerMapper HANDLERS = new HandlerMapper();
    private final EntityType entity;
    private final Position position;

    public EntityEmergedEvent(EntityType entity, Position position) {
        this.entity = entity;
        this.position = position;
    }

    public EntityType getEntity() {
        return entity;
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public HandlerMapper getMapper() {
        return HANDLERS;
    }
}
