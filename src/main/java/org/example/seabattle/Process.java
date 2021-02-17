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
    private final List<Sector> dummies = new ArrayList<>();
    private final List<Sector> potentialTargets = new ArrayList<>();
    private final List<Sector> leftToShoot = new LinkedList<>();
    private final List<Ship> fleet = new LinkedList<>();
    private final List<Ship> p1fleet = new ArrayList<>();
    private final List<Ship> p2fleet = new ArrayList<>();
    private final List<Sector> fleet1hulls = new ArrayList<>();
    private final List<Sector> fleet2hulls = new ArrayList<>();
    private final String[] types = {
            "carrier", "cruiser1", "cruiser2", "sub1", "sub2", "sub3", "heli1", "heli2", "heli3", "heli4" };
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

    // surrounds unit with proximity sectors which deny placement of another unit on them
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

        assert sectorList != null; // done according to Intellij suggestion
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

    // makes projection of sectors occupied by unit based on unit type, heading and origin sector.
    // works in detection and deployment mode (places the unit - transforms sectors as taken ones).
    void alignHull(List<Sector> playerField, Sector passedSector, Ship exeShip, boolean deploying) {
        if (deploying) {
            this.placementAllowed = true;
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
        }           // collision detection block
        for (Sector sectorChecked : playerField) {
            String sc = sectorChecked.getStatus();
            if (sc.equals("proximity") || sc.equals("hull") || sc.equals("origin")) {
                int checkX = sectorChecked.getCoordinateX();
                int checkY = sectorChecked.getCoordinateY();
                for (Sector dummySector : dummies) {
                    int dummyX = dummySector.getCoordinateX();
                    int dummyY = dummySector.getCoordinateY();
                    if (checkX == dummyX && checkY == dummyY) {
                        free = false;
                        break;
                    }
                }
            }
        }
        if (deploying) {
            passedSector.setStatus("origin");
        }
        dummies.clear();
        this.placementAllowed = free && allSectorsUsed;
    }

    void placeUnit(int locationX, int locationY) {
        unitInProcess.setLocationX(locationX);
        unitInProcess.setLocationY(locationY);
        if (getGamestate() == 1) {
            p1fleet.add(unitInProcess);
        }
        if (getGamestate() == 2) {
            p2fleet.add(unitInProcess);
        }
        fleet.remove(unitInProcess);
        this.unitInProcess = null;
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
            extractedMethod(ship, x, y, p1sectors, p1fleet);
        }
        if (gamestate == 2) {
            extractedMethod(ship, x, y, p2sectors, p2fleet);
        }
    this.placementAllowed = false;
    }

    private void extractedMethod(Ship ship, int x, int y, List<Sector> pSectors, List<Ship> pFleet) {
        Sector aSector = null;
        for (Sector sector : pSectors) {
            if (sector.getCoordinateX() == x && sector.getCoordinateY() == y) {
                aSector = sector;
            }
        }
        alignHull(pSectors, aSector, ship, false);
        if (placementAllowed) {
            pFleet.add(ship);
            fleet.remove(ship);
            assert aSector != null; // done according to Intellij suggestion
            setupProximity(aSector, ship);
            alignHull(pSectors, aSector, ship, true);
        }
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
        if (sectorInProcess != null) {
            Sector lastShot = sectorInProcess;
            removeSector(leftToShoot, lastShot);
// flagging sectors to dummy and potential targets
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
            for (Sector dummy : dummies) {
                removeSector(leftToShoot, dummy);
                removeSector(potentialTargets, dummy);
            }
        }
// shooting sectors taken from potential targets list
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
// shooting sectors taken from "left to shoot list" - random draw
            if (leftToShoot.size() > 0) {
                index = random.nextInt(leftToShoot.size());
                currentTarget = findSector(p1sectors, leftToShoot.get(index));
            } else {
                if (fleet1hulls.size() > 0) {
                    Sector oneHull = fleet1hulls.get(0);
                    currentTarget = findSector(p1sectors, oneHull);
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
    }

    // uses x, y of sample sector to search such sector in sectorList given as argument
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
        for (String unitName : types) {
            int sectorCount = 0;
            for (Sector sector : fleetHullSectors) {
                if (sector.getTakenBy().equals(unitName) &&
                        sector.getStatus().startsWith("concealed")) {
                    sectorCount++;
                }
            }
            if (sectorCount < 1) {
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

    public List<Sector> getFleet1hulls() {
        return fleet1hulls;
    }

    public List<Sector> getFleet2hulls() {
        return fleet2hulls;
    }

    public void allowPlacement(boolean allowed) {
        this.placementAllowed = allowed;
    }

    public void setGamestate(int gamestate) {
        this.gamestate = gamestate;
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