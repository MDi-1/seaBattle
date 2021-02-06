package org.example.seabattle;

public class Ship {
    private final String shipType;
    private String shipStatus; // hidden, sunk, exposed
    private int damage;
    private int heading = 0;
    private int shipSize;
    private int locationX = 0;
    private int locationY = 0;

    public Ship(String type) {
        this.shipType = type;

        switch (type.substring(0, 3)) {
            case "car":  this.shipSize = 4;   break;
            case "cru":  this.shipSize = 3;  break;
            case "sub":  this.shipSize = 2;  break;
            case "hel":  this.shipSize = 1;  break;
        }
    }

    public String getShipType() {
        return shipType;
    }

    public String getShipStatus() {
        return shipStatus;
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

    public int getLocationX() {
        return locationX;
    }

    public int getLocationY() {
        return locationY;
    }

    public void setShipStatus(String shipStatus) {
        this.shipStatus = shipStatus;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setHeading(int heading) {
        this.heading = heading;
    }

    public void setLocationX(int locationX) {
        this.locationX = locationX;
    }

    public void setLocationY(int locationY) {
        this.locationY = locationY;
    }

}
