package org.example.seabattle;
/*
Do zrobienia później - jak starczy czasu zastosować "stream-y"
*/
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Process {
    private int gamestate = -1;
    private int score1 = 0;
    private int score2 = 0;
    private Ship unitInProcess;
    private Sector sectorInProcess;
    private boolean placementAllowed = true;
    private boolean fireFree = false;
    private int sunkQuantity = 0;
    private final List<Sector> p1sectors = new LinkedList<>();
    private final List<Sector> p2sectors = new LinkedList<>();
    private List<Sector> dummies = new ArrayList<>();
    private List<Sector> potentialTargets = new ArrayList<>();
    private List<Sector> leftToShoot = new LinkedList<>();
    private List<Ship> fleet = new LinkedList<>();
    private List<Ship> p1fleet = new ArrayList<>();
    private List<Ship> p2fleet = new ArrayList<>();
    private final String[] types = {
            "carrier", "cruiser1", "cruiser2", "sub1", "sub2", "sub3", "heli1", "heli2", "heli3", "heli4" };
    private List<Sector> fleet1hulls = new ArrayList<>();
    private List<Sector> fleet2hulls = new ArrayList<>();
    Service service = new Service(); // delete this as soon as app is ready for production.
    Random random = new Random();

    public Process() {
        for (int i = 0; i < 100; i ++) {
            int y = i / 10;
            int x = i - y * 10;
            p1sectors.add(new Sector(1, y, x));
            p2sectors.add(new Sector(2, y, x));
        }
    }

    void createTmpFleet() {
        fleet.clear();
        for (String newUnit : types) {
            Ship theUnit = new Ship(newUnit);
            fleet.add(theUnit);
        }
    }

    void pickUnit(String fullType) {
        for (Ship pickedUnit : fleet) {
            if (fullType.substring(0, 3).equals(pickedUnit.getShipType().substring(0, 3))) {
                this.unitInProcess = pickedUnit;
            }
        }
    }

    public void setupProximity(Sector sector, Ship exeShip) {
        List<Sector> sectorList = null;
        if (sector.getPlayer() == 1) {
            sectorList = p1sectors;
        }
        if (sector.getPlayer() == 2) {
            sectorList = p2sectors;
        }
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

    void alignHull(List<Sector> playerField, Sector passedSector, Ship exeShip, boolean deploying) {
        if (deploying) {
            this.placementAllowed = true;
//            System.out.println(" ]]] f.in deployment mode. [[[");
        } else {
//            System.out.println(" ]]] f.in checking mode. [[[");
        }
        int x = passedSector.getCoordinateX();
        int y = passedSector.getCoordinateY();
        int player = passedSector.getPlayer();
        String name = exeShip.getShipType();
        int unitSize = exeShip.getShipSize();
        int heading = exeShip.getHeading();
        int offset = (unitSize + 1) / 4;
        boolean allSectorsUsed = false;
        boolean free = true;
        int sectorUsage = 0;
//        System.out.print("modifiers:");
        for (Sector iteratedSector : playerField) {
            int setupX = iteratedSector.getCoordinateX();
            int setupY = iteratedSector.getCoordinateY();
            int modifierX = 0;
            int modifierY = 0;
            for (int n = 0; n < unitSize; n ++) {
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
//                    System.out.print(" X= " + modifierX + "; Y= " + modifierY + " / ");
                    if (deploying) {
                        iteratedSector.setStatus("hull");
                        iteratedSector.setTakenBy(name);
                        if (player == 1) {
                            fleet1hulls.add(iteratedSector);
                        }
                        if (player == 2) {
                            fleet2hulls.add(iteratedSector);
                        }
                    } else {
                        Sector dummy = new Sector(0, resultX, resultY);
                        dummies.add(dummy);
                    }
                    sectorUsage++;
                }
                if (sectorUsage == unitSize) {
                    allSectorsUsed = true;
                }
            }
        } // blok detekcji kolizji
        int proxCount = 0;
//        System.out.print("\ndummies: ");
//        for (Sector dummySector : dummies) {
//            System.out.print(" / x=" + dummySector.getCoordinateX() + "; y=" + dummySector.getCoordinateY());
//        }
//        System.out.println(" >>. end of dummies");
//        System.out.print("checked sectors:");
        for (Sector sectorChecked : playerField) {
            String sc = sectorChecked.getStatus();
            if (sc.equals("proximity") || sc.equals("hull") || sc.equals("origin")) {
                proxCount ++;
                int checkX = sectorChecked.getCoordinateX();
                int checkY = sectorChecked.getCoordinateY();
                for (Sector dummySector : dummies) {
                    int dummyX = dummySector.getCoordinateX();
                    int dummyY = dummySector.getCoordinateY();
//                    System.out.print("/  x=" + checkX + "; y=" + checkY + " ");
                    if (checkX == dummyX && checkY == dummyY) {
//                        System.out.println(" >>> COLLISION <<<");
                        free = false;
                    }
                }
            }
        }
        if (deploying) {
            passedSector.setStatus("origin");
        }
//        System.out.print("\nprox count= " + proxCount + " // sectors placed= " + sectorUsage +
//           "; size = " + unitSize + ";  dummies: " + dummies.size() + "./ ");
        dummies.clear();
//        System.out.println(" states: free?=" + free + "; all used?=" + allSectorsUsed + "./ ");
        this.placementAllowed = free && allSectorsUsed;
    }

    void placeUnit(int locationX, int locationY) {
        unitInProcess.setLocationX(locationX);
        unitInProcess.setLocationY(locationY);
        if (getGamestate() == 1) {
            p1fleet.add(unitInProcess);
        } // później spróbować przechowywać jednostki w tablicy zamiast w liście
        if (getGamestate() == 2) {
            p2fleet.add(unitInProcess);
        }
        fleet.remove(unitInProcess);
        this.unitInProcess = null;

//        System.out.print("fleet to be placed: ");
//        for (Ship ship : fleet) System.out.print(ship.getShipType() + "; ");
//        System.out.println();
    }

    int rotateUnit() {
        int direction = 45;
        if (unitInProcess != null ) {
            direction = unitInProcess.getHeading();
        } else {
            return direction;
        }
        if (direction == 0) {
            direction = 270;
        } else {
            direction = 0;
        }
        unitInProcess.setHeading(direction);
        return direction;
    }

    void autoDeploySingleUnit() {
        // tu okazuje się że fleet zamiast być listą powinna być kolejką FIFO
        Ship ship = fleet.get(0);
        int x = random.nextInt(10);
        int y = random.nextInt(10);
        boolean h = random.nextBoolean();
        int heading;
        if (h) {
            heading = 0;
        } else {
            heading = 270;
        }
        ship.setLocationX(x);
        ship.setLocationY(y);
        ship.setHeading(heading);
        if (gamestate == 1) {
            Sector aSector = null;
            for (Sector sector : p1sectors) {
                if (sector.getCoordinateX() == x && sector.getCoordinateY() == y) {
                    aSector = sector;
                }
            }
            alignHull(p1sectors, aSector, ship, false);
            if (placementAllowed) {
                p1fleet.add(ship);
                fleet.remove(ship);
                setupProximity(aSector, ship);
                alignHull(p1sectors, aSector, ship, true);
            }
        }
        if (gamestate == 2) {
            Sector aSector = null;
            for (Sector sector : p2sectors) {
                if (sector.getCoordinateX() == x && sector.getCoordinateY() == y) {
                    aSector = sector;
                }
            }
            alignHull(p2sectors, aSector, ship, false);
            if (placementAllowed) {
                p2fleet.add(ship);
                fleet.remove(ship);
                setupProximity(aSector, ship);
                alignHull(p2sectors, aSector, ship, true);
            }
        }
    this.placementAllowed = false;
    }

    void autoDeployAll() {
        for (int i = 0; i < 9999; i ++) {
            autoDeploySingleUnit();
            if (fleet.size() < 1) {
                return;
            }
        }
    }

    void rebuildSectors() {
        int i = 2;
        List<Sector> sectorList = null;
        while (i > 0) {
            if (i == 2) {
                sectorList = p2sectors;
            }
            if (i == 1) {
                sectorList = p1sectors;
            }
            for (Sector sector : sectorList) {
                switch (sector.getStatus()) {
                    case "proximity":
                    case "nothing":
                        sector.setStatus("concealed_clear");
                        break;
                    case "hull":
                        sector.setStatus("concealed_hull");
                        break;
                    case "origin":
                        sector.setStatus("concealed_origin");
                }
                if (sector.getPlayer() == 1) {
                    leftToShoot.add(sector);
                }
            } i --;
        }
    }

    Sector computerIsShooting(boolean wasDestroyed) {
// trzeba napisać usuwanie sektorów- końcówek
// jeśli komputer zorientował się że zatopił całą jednostkę

// trzeba jeszcze podać komputerowi informację
// ile zostało jednostek do zatopienia (- po trafionym zatopionym)

// usuwanie sektorów "exposed" z listy pozostałych do strzału
        if (sectorInProcess != null) {
            Sector lastShot = sectorInProcess;
            service.backupList.add(lastShot);
            removeSector(leftToShoot, lastShot);
// flagowanie na puste (dummy) i potencjalne cele
            if (lastShot.getStatus().equals("exposed_hull") || lastShot.getStatus().equals("exposed_origin")) {
                int[] dx = {-1, 0, 1, 1, 1, 0, -1, -1};
                int[] dy = {-1, -1, -1, 0, 1, 1, 1, 0};
                boolean swap = true;
                for (int n = 0; n < 8; n++) {
                    int newX = lastShot.getCoordinateX() + dx[n];
                    int newY = lastShot.getCoordinateY() + dy[n];
                    if (newX < 0 || newY < 0 || newX > 9 || newY > 9) {

                    } else {
                        Sector flagged = new Sector(5, newX, newY);
                        if (swap) {
                            dummies.add(flagged);
                        } else {
                            potentialTargets.add(flagged);
                        }
                        swap = !swap;
                    }
                }
                multiplication(potentialTargets, leftToShoot);
                if (wasDestroyed) {
                    dummies.addAll(potentialTargets);
                    potentialTargets.clear();
                }
            }
// usuwanie obliczonych jako puste (dummy) z listy pozostałych do strzału
            for (Sector dummy : dummies) {
                removeSector(leftToShoot, dummy);
                removeSector(potentialTargets, dummy);
            }
        }
// strzelanie do sektorów obliczonych jako potencjalne cele
        Sector currentTarget = null;
        int index;
        if (potentialTargets.size() > 0) {
            Sector tmpTarget = potentialTargets.get(0);
            potentialTargets.remove(0);
            int tmpX = tmpTarget.getCoordinateX();
            int tmpY = tmpTarget.getCoordinateY();
            for (Sector sector : p1sectors) { // kandydat na użycie findSector() ?
                if (sector.getCoordinateX() == tmpX && sector.getCoordinateY() == tmpY) {
                    currentTarget = sector;
                }
            }
        } else {
// losowanie sektora z listy do strzału
            if (leftToShoot.size() > 0) {
                index = random.nextInt(leftToShoot.size());
                currentTarget = findSector(p1sectors, leftToShoot.get(index));

            } else {
                if (fleet1hulls.size() > 0) {
                    System.out.println("!ERROR! leftToShoot is empty");
                    Sector oneHull = fleet1hulls.get(0);
                    currentTarget = findSector(p1sectors, oneHull);
                } else {
                }
            }
        }
        this.sectorInProcess = null;
        return currentTarget;
    }

    void removeSector(List<Sector> toSubtractFrom, Sector sector) {
        int x = sector.getCoordinateX();
        int y = sector.getCoordinateY();
        toSubtractFrom.removeIf(
                toRemove -> toRemove.getCoordinateX() == x && toRemove.getCoordinateY() == y);
    }

    void multiplication(List<Sector> listToPickFrom, List<Sector> samples) {
        List<Sector> tmpList = new LinkedList<>();
        for (Sector sample : samples) {
            int x = sample.getCoordinateX();
            int y = sample.getCoordinateY();
            for (Sector sector : listToPickFrom) {
                if (sector.getCoordinateX() == x && sector.getCoordinateY() == y) {
                    tmpList.add(sector);
                }
            }
        }
        listToPickFrom.clear();
        listToPickFrom.addAll(tmpList);
        System.out.println();
    }


    // wyszukuje i zwraca po x, y
    Sector findSector(List<Sector> sectorList, Sector sectorSample) {
        int x = sectorSample.getCoordinateX();
        int y = sectorSample.getCoordinateY();
        Sector sectorToGet = null;
        for (Sector sector : sectorList) {
            if (sector.getCoordinateX() == x && sector.getCoordinateY() == y) {
                sectorToGet = sector;
            }
        } return sectorToGet;
    }

    int evaluate() {
        int winner = 0;
        this.score1 = 20 - fleet2hulls.size();
        this.score2 = 20 - fleet1hulls.size();
        if (score1 > score2) {
            winner = 1;
        }
        if (score2 > score1) {
            winner = 2;
        }
        if (score1 == score2) {
            winner = 0;
        }
        return winner;
    }

    int defineSunk(int playerToShootAt) {
        List<Sector> fleetHullSectors;
        List<Ship> playerShips;
        if (playerToShootAt == 1) {
            fleetHullSectors = fleet1hulls;
            playerShips = p1fleet;
        } else {
            fleetHullSectors = fleet2hulls;
            playerShips = p2fleet;
        }
//        Sector testedSector = null;
        for (String unitName : types) {
//            System.out.print("  defineSunk() count taken sectors for " + unitName + ": ");
            int sectorCount = 0;
            for (Sector sector : fleetHullSectors) {
//                testedSector = sector;
                if (sector.getTakenBy().equals(unitName) &&
                        sector.getStatus().startsWith("concealed")) {
                    sectorCount++;

                    // test blok
                    int x = sector.getCoordinateX();
                    int y = sector.getCoordinateY();
                    char a = (char) (y + 65);
//                    System.out.print("[" + a  + (x + 1) + "] ");
                }
            }
//            System.out.println("SECTOR COUNT= " + sectorCount);
            if (sectorCount < 1) {
                //System.out.println("setting sunk status for " + unitName);
                for (Ship ship : playerShips) {
                    if (ship.getShipType().equals(unitName)) {
                        ship.setSunk(true);
                    }
                }
            }
        }
        int i = 0;
        for (Ship ship : playerShips) {
            if (ship.getSunk()) {
                i ++;
            }
        }
//        System.out.println("  defineSunk() i=" + i + " units sunk");
        return i;
    }

    boolean isFireFree() {
        return fireFree;
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

    public int getScore1() {
        return score1;
    }

    public int getScore2() {
        return score2;
    }

    public List<Ship> getFleet() {
        return fleet;
    }

    public boolean isPlacementAllowed() {
        return placementAllowed;
    }

    public void allowFire(boolean fireFree) {
        this.fireFree = fireFree;
    }

    public List<Ship> getP1fleet() {
        return p1fleet;
    }

    public List<Ship> getP2fleet() {
        return p2fleet;
    }

    public List<Sector> getDummies() {
        return dummies;
    }

    public List<Sector> getFleet1hulls() {
        return fleet1hulls;
    }

    public List<Sector> getFleet2hulls() {
        return fleet2hulls;
    }

    public List<Sector> getLeftToShoot() {
        return leftToShoot;
    }

    public List<Sector> getPotentialTargets() {
        return potentialTargets;
    }

    public void allowPlacement(boolean allowed) {
        this.placementAllowed = allowed;
    }

    public void setGamestate(int gamestate) {
        this.gamestate = gamestate;
    }

    public Sector getSectorInProcess() {
        return sectorInProcess;
    }

    public void setSectorInProcess(Sector sect) {
        this.sectorInProcess = sect;
    }

    public int getSunkQuantity() {
        return sunkQuantity;
    }

    public void setSunkQuantity(int sunkQuantity) {
        this.sunkQuantity = sunkQuantity;
    }
}
