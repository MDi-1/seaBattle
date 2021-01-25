package org.example.seabattle;
/*
program posiada opcję gry na 2 graczy - dlatego nie będzie 2 osobnych funkcji na ruch gracza i ruch komputera, tylko ruch gracza oznaczony jako AI będzie uzupełniony o ruch AI.

task - jak starczy czasu zastosować "stream-y"
*/
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Process {
    // typ na razie nie wiadomo jaki
    // 0 = placement; 1 = tura gracz1; 2 = tura gracz2
    private int gamestate = 0;
    private List<Sector> p1area = new ArrayList<>();
    private List<Sector> p2area = new ArrayList<>();
    List<Ship> p1fleet = new LinkedList<>();
    List<Ship> p2fleet = new LinkedList<>();


    public Process() {
        for (int i = 0; i < 100; i ++) {
            int y = i / 10;
            int x = i - y * 10;
            p1area.add(new Sector(1, y, x));
            p2area.add(new Sector(2, y, x));
        }
    }

    List<Ship> createUnits() {
//        String[] types = {
//                "carrier", "cruiser1", "cruiser2", "sub1", "sub2", "sub3", "heli1", "heli2", "heli3", "heli4" };

//      todo - tymczasowa tablica, ta właściwa jest wykomentowana powyżej
        String[] types = {
               "sub1", "sub2", "sub3", "heli1", "heli2", "heli3", "heli4" };
        for (String newUnit : types) {
            Ship theUnit = new Ship(newUnit);
            p1fleet.add(theUnit);
        } return p1fleet;
    }



    //przeciągnij i upuść jednostkę
    Ship pickUnit() {
        // task - ta funkcja to jest tylko zaślepka
        return p1fleet.get(4);
    }

    // funkcja lokowania jednostek
    // void placeUnit(Ship ship) {
    // p1fleet.remove(ship.getType());
    // }

    //funkcja salwy
    int shoot(Sector sector, int gamestate) {
        return 0;
    }

    //funkcja wykrywania trafień
    boolean isDamaged(Sector sector, Ship ship, int shoot_return) {
        return false;
    }

    //funkcja zatopienia jednostki
    boolean isDestroyed(Sector sector, Ship ship) {
        return false;
    }

    public int getGamestate() {
        return gamestate;
    }

    public List<Sector> getP1area() {
        return p1area;
    }

    public List<Sector> getP2area() {
        return p2area;
    }

    //funkcja ustalenia wygranego
    void getWinner(int gamestate) {}
}
