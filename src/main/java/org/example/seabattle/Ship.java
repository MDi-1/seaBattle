package org.example.seabattle;

public class Ship {
    private String type;
    private short orientation;
    private int damage;

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
