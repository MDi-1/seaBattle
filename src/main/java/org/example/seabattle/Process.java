package org.example.seabattle;
/*
program posiada opcję gry na 2 graczy - dlatego nie będzie 2 osobnych funkcji na ruch gracza i ruch komputera, tylko ruch gracza oznaczony jako AI będzie uzupełniony o ruch AI.

Do zrobienia później - jak starczy czasu zastosować "stream-y"
*/
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Process {
    // typ na razie nie wiadomo jaki
    // 0 = placement; 1 = tura gracz1; 2 = tura gracz2
    private int gamestate = 0;
    private Ship unitInProcess;
    private List<Sector> p1sectors = new LinkedList<>();
    private List<Sector> p2sectors = new LinkedList<>();
    private List<Sector> dummies = new ArrayList<>();
    List<Ship> fleet = new LinkedList<>();

    public Process() {
        for (int i = 0; i < 100; i ++) {
            int y = i / 10;
            int x = i - y * 10;
            p1sectors.add(new Sector(1, y, x));
            p2sectors.add(new Sector(2, y, x));
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
            if (fullType.substring(0, 3).equals(pickedUnit.getShipType().substring(0, 3))) {
                this.unitInProcess = pickedUnit;
                System.out.print(pickedUnit.getShipType() + "; ");
            }
        } System.out.println("gamestate= " + gamestate);
    }

    public void setupProximity(Sector sector, Ship exeShip) {
        int unitSize = exeShip.getShipSize();
        int heading = exeShip.getHeading();
        int offset = (unitSize + 1) / 4;

        int outboundW, outboundE, outboundN, outboundS;
        outboundW = outboundE = outboundN = outboundS = 0;
        if (heading == 0) {
            outboundN = offset;
            outboundS = unitSize / 2;
        }
        if (heading == 270) {
            outboundW = offset;
            outboundE = unitSize / 2;
        }
        int x = sector.getCoordinateX();
        int y = sector.getCoordinateY();
        List<Sector> sectorList = getP1sectors();

        for (Sector modifiedSector : sectorList) {
            int setupX = modifiedSector.getCoordinateX();
            int setupY = modifiedSector.getCoordinateY();
            for (int u =  -1 - outboundW; u <= 1 + outboundE; u ++) {
                for (int v = -1 - outboundN; v <= 1 + outboundS; v ++) {
                    if (u != 0 || v != 0) {
                        if (setupX == (x + u) && setupY == (y + v)) {
                            modifiedSector.setStatus("proximity");
                        }
                    }
                }
            }
        }
    }

    boolean alignHull(Sector passedSector, Ship exeShip, boolean deploying) {
        if (deploying) System.out.println("-function alignHull in deploying mode-");
        boolean allSectorsUsed = false;
        int unitSize = exeShip.getShipSize();
        int heading = exeShip.getHeading();
        int offset = (unitSize + 1) / 4;
        int x = passedSector.getCoordinateX();
        int y = passedSector.getCoordinateY();
        int sectorUsage = 0;
        String st1 = "";
        String st2 = "";
        for (Sector iteratedSector : p1sectors) {
            int setupX = iteratedSector.getCoordinateX();
            int setupY = iteratedSector.getCoordinateY();
            for (int n = 0; n < unitSize; n ++) {
                int modifierX = 0;
                int modifierY = 0;
                switch (heading) {
                    case 0:
                        modifierX = 0;
                        modifierY = n - offset;
                        break;
                    case 270:
                        modifierX = n - offset;
                        modifierY = 0;
                        break;
                    default:
                }
                if ((setupX == x + modifierX) && (setupY == y + modifierY)) {
                    int resultX = x + modifierX;
                    int resultY = y + modifierY;
                    if (deploying) {
                        iteratedSector.setStatus("hull");
                    } else {
                        Sector dummy = new Sector(0, resultX, resultY);
                        dummies.add(dummy);
                    }// task - zanim ten blok będzie znowu wykonany listę dummies trzeba rozładować.
                    //System.out.print("parsing: x= " + resultX + "; y= " + resultY + " ");
                    sectorUsage++;
                }
                if (sectorUsage == unitSize) {
                    allSectorsUsed = true;
                }
            }
        }
        boolean free = true;
        int proxCount = 0;
        System.out.print("dummies: ");

        for (Sector dummySector : dummies) {
            System.out.print(" / x=" + dummySector.getCoordinateX() + "; y=" + dummySector.getCoordinateY());
        }
        System.out.println(" / end of dummies");

        for (Sector sectorChecked : p1sectors) {
            if (sectorChecked.getStatus().equals("proximity")) {
                proxCount ++;
                int checkX = sectorChecked.getCoordinateX();
                int checkY = sectorChecked.getCoordinateY();
                for (Sector dummySector : dummies) {
                    int dummyX = dummySector.getCoordinateX();
                    int dummyY = dummySector.getCoordinateY();
                    System.out.println(
                            "checked x=" + checkX + "; y=" + checkY + " / dummy: x=" + dummyX + "; y=" + dummyY);
                    if (checkX == dummyX && checkY == dummyY) {
                        System.out.println("COLLISION");
                        free = false;
                    }
                }
            }
        }

        if (deploying) {
            passedSector.setStatus("origin");
        }
        System.out.println("prox sectors count= " + proxCount + " // sectors placed= " + sectorUsage +
           "; unitsize = " + unitSize + ";  dummy list length: " + dummies.size());
        dummies.clear();
        return free && allSectorsUsed;
    }

    void placeUnit(int locationX, int locationY) {
        String type = unitInProcess.getShipType(); // this line is just for println
        unitInProcess.setLocationX(locationX);
        unitInProcess.setLocationY(locationY);
        fleet.remove(unitInProcess);
        this.unitInProcess = null;
        //System.out.println("unit removed: " + type);
    }

    int rotateUnit() {
        int direction = 45;
        if (getUnitInProcess() != null ) {
            direction = getUnitInProcess().getHeading();
        } else {
            return direction;
        }
        if (direction == 0) {
            direction = 270;
        } else {
            direction = 0;
        }
        getUnitInProcess().setHeading(direction);
        return direction;
    }

    //funkcja salwy - być może do kasacji bo jest w Execlass
    int fire(Sector sector, int gamestate) {
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

    public List<Sector> getP1sectors() {
        return p1sectors;
    }

    public List<Sector> getP2sectors() {
        return p2sectors;
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
