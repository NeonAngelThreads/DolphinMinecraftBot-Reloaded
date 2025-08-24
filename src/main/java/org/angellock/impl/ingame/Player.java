package org.angellock.impl.ingame;

import org.angellock.impl.util.math.Position;

public class Player implements IPlayer{
    private final int id;
    private String name;
    private Position position;

    public Player(int id, Position position) {
        this.id = id;
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Position getPosition() {
        return position;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
    public void setPosition(double x, double y, double z) {
        this.position.setX(x);
        this.position.setY(y);
        this.position.setZ(z);
    }

    @Override
    public double getDistanceFromOthers(IPlayer player) {
        return position.getDistance(player.getPosition());
    }
}
