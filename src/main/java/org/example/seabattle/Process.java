package org.example.seabattle;
/*
program posiada opcję gry na 2 graczy - dlatego nie będzie 2 osobnych funkcji na ruch gracza i ruch komputera, tylko ruch gracza oznaczony jako AI będzie uzupełniony o ruch AI.

task - jak starczy czasu zastosować "stream-y"
*/
import java.util.LinkedList;
import java.util.List;

public class Process {
    // typ na razie nie wiadomo jaki
    // 0 = placement; 1 = tura gracz1; 2 = tura gracz2
    private int gamestate = 0;
    private Ship unitInProcess;
    private List<Sector> p1area = new LinkedList<>();
    private List<Sector> p2area = new LinkedList<>();
    List<Ship> fleet = new LinkedList<>();

    // coś tu jest pomylone z X i Y, albo sidePaneValueX i sidePaneValueY
    public Process() {
        for (int i = 0; i < 100; i ++) {
            int y = i / 10;
            int x = i - y * 10;
            p1area.add(new Sector(1, y, x));
            p2area.add(new Sector(2, y, x));
        }
//        int sidePaneValueX = 0;
//        int sidePaneValueY = 0;
//        for (int i = 0; i < 40; i ++) {
//            if (sidePaneValueX > 4) {
//                sidePaneValueX = 0;
//                sidePaneValueY ++;
//            }
//            sidePaneList.add(new Sector(0, sidePaneValueX, sidePaneValueY));
//            sidePaneValueX ++;
//        }
    }

    List<Ship> createUnits() {
        String[] types = {
             "carrier", "cruiser1", "cruiser2", "sub1", "sub2", "sub3", "heli1", "heli2", "heli3", "heli4" };
        for (String newUnit : types) {
            Ship theUnit = new Ship(newUnit);
            fleet.add(theUnit);
        } return fleet;
    }
    void pickUnit(String fullType) {
        for (Ship pickedUnit : fleet) {
            System.out.print(pickedUnit.getType() + "; ");
            if (fullType.substring(0, 3).equals(pickedUnit.getType().substring(0, 3))) {
                this.unitInProcess = pickedUnit;
            }
        } System.out.println("gamestate= " + gamestate);
    }

     String placeUnit() {
        String type = unitInProcess.getType().substring(0, 3);
        fleet.remove(unitInProcess);
        this.unitInProcess = null;
        if (fleet.size() == 0) {
            this.gamestate ++;
        }
        return type;
     }

    //funkcja salwy - być może do kasacji bo jest w Execlass
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

    public Ship getUnitInProcess() {
        return unitInProcess;
    }

    public List<Sector> getP1area() {
        return p1area;
    }

    public List<Sector> getP2area() {
        return p2area;
    }

    public List<Ship> getFleet() {
        return fleet;
    }

    //funkcja ustalenia wygranego
    void getWinner(int gamestate) {}

    public void setGamestate(int gamestate) {
        this.gamestate = gamestate;
    }
}
