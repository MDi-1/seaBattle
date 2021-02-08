package org.example.seabattle;

public class Sector {
    private final int player;
    private final int coordinateX;
    private final int coordinateY;
    private String status = "nothing";


    public Sector(int player, int coordinateX, int coordinateY) {
        this.player = player;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
    }

    public int getPlayer() {
        return player;
    }

    public String getStatus() {
        return status;
    }

    public int getCoordinateX() {
        return coordinateX;
    }

    public int getCoordinateY() {
        return coordinateY;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "player= " + player + ", x= " + coordinateX + ", y= " + coordinateY + ", st= " + status;
    }
}
