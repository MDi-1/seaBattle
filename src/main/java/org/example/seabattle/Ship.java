package org.example.seabattle;

public class Ship {
    private final String type;
    private final short orientation;
    private final int damage;

    public Ship(String type, short orientation, int damage) {
        this.type = type;
        this.orientation = orientation;
        this.damage = damage;
    }

    public String getType() {
        return type;
    }

    public short getOrientation() {
        return orientation;
    }

    public int getDamage() {
        return damage;
    }
}
