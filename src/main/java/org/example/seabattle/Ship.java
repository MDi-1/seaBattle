package org.example.seabattle;

public class Ship {
    private String shipType;
    private int damage;
    private int heading = 0;
    private int shipSize;

    public Ship(String type) {
        this.shipType = type;

        switch (type.substring(0, 3)) {
            case "car":  this.shipSize = 4;  break;
            case "cru":  this.shipSize = 3;  break;
            case "sub":  this.shipSize = 2;  break;
            case "hel":  this.shipSize = 1;  break;
        }
    }

    public String getShipType() {
        return shipType;
    }

    public int getDamage() {
        return damage;
    }

    public int getHeading() {
        return heading;
    }

    public int getShipSize() {
        return shipSize;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setHeading(int heading) {
        this.heading = heading;
    }

    public void setupSectors(String shipType) {

    }
}
