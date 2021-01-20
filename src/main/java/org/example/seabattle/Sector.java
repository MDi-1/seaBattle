package org.example.seabattle;

public class Sector {
    private int coordinateX;
    private int coordinateY;
    private char status;

    public Sector(int coordinateX, int coordinateY, char status) {
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.status = status;
    }

    public int getCoordinateX() {
        return coordinateX;
    }

    public int getCoordinateY() {
        return coordinateY;
    }

    public char getStatus() {
        return status;
    }
}
