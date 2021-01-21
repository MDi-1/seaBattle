package org.example.seabattle;

public class Sector {
    private int coordinateX;
    private int coordinateY;
    private String status;

    public Sector(int coordinateX, int coordinateY, String status) {
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

    public String getStatus() {
        return status;
    }
}
