package org.example.seabattle;

public class Sector {
    private final int player;
    private final int coordinateX;
    private final int coordinateY;
    private String status;


    public Sector(int player, int coordinateY, int coordinateX) {
        this.player = player;
        this.coordinateY = coordinateY;
        this.coordinateX = coordinateX;
    }

    public String getStatus() {
        return status;
    }

    public int getCoordinateY() {
        return coordinateY;
    }

    public int getCoordinateX() {
        return coordinateX;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "coordinateY=" + coordinateY + ", coordinateX=" + coordinateX;
    }
}
