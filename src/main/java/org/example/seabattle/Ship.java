package org.example.seabattle;

public class Ship {
    private final String type;
    private int orientation;
    private int damage;

    public Ship(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public int getOrientation() {
        return orientation;
    }

    public int getDamage() {
        return damage;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
}
