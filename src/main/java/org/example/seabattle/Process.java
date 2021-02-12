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
    private final List<Sector> p1sectors = new LinkedList<>();
    private final List<Sector> p2sectors = new LinkedList<>();
    private List<Sector> dummies = new ArrayList<>();
    private List<Sector> potentialTargets = new ArrayList<>();
    private List<Sector> leftToShoot = new LinkedList<>();
    private List<Ship> fleet = new LinkedList<>();
    private List<Ship> p1fleet = new ArrayList<>();
    private List<Ship> p2fleet = new ArrayList<>();

    public Process() {
        for (int i = 0; i < 100; i ++) {
            int y = i / 10;
            int x = i - y * 10;
            p1sectors.add(new Sector(1, y, x));
            p2sectors.add(new Sector(2, y, x));
            leftToShoot.add(new Sector(2, y, x));
        }
    }

    List<Ship> createUnits() {
        fleet.clear();
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
        Random random = new Random();
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
            }
            i --;
        }
    }

    Sector computerIsShooting() {
// trzeba napisać usuwanie sektorów- końcówek
// jeśli komputer zorientował się że zatopił całą jednostkę
// w sumie tą funkcjonalność można napisać dla obu graczy
// (zwaną "trafiony zatopiony) i gracz komputerowy to wykorzysta

// trzeba jeszcze podać komputerowi informację
// ile zostało jednostek do zatopienia (- po trafionym zatopionym)

// wyrzucanie jako text na konsolę listy obliczonych jako puste
        System.out.print("dummies: ");
        for (Sector dummy : dummies) {
            int dummyX = dummy.getCoordinateY();
            int dummyY = dummy.getCoordinateY();
            System.out.print("x= " + dummyX + "; y= " + dummyY);
        }
        System.out.println();


// usuwanie odkrytych z listy pozostałych do strzału
        Sector sectorInLastShot = sectorInProcess;
        removeSector(sectorInLastShot);
        /*
        for (Sector exposedSector : p1sectors) {
            if (exposedSector.getStatus().startsWith("exposed")) {
                int x = exposedSector.getCoordinateX();
                int y = exposedSector.getCoordinateY();
                for (Sector sectorToRemove : leftToShoot) {
                    if (sectorToRemove.getCoordinateX() == x
                            && sectorToRemove.getCoordinateY() == y) {
                        leftToShoot.remove(sectorToRemove);
                    }
                }
            }
        }
         */


 // usuwanie obliczonych jako puste (dummy) z listy pozostałych do strzału
        for (Sector dummy : dummies) {
            removeSector(dummy);
        }
        /*
        for (Sector dummy : dummies) {
            int dummyX = dummy.getCoordinateY();
            int dummyY = dummy.getCoordinateY();
            for (Sector sectorToRemove : leftToShoot) {
                if (sectorToRemove.getCoordinateX() == dummyX
                        && sectorToRemove.getCoordinateY() == dummyY) {
                    leftToShoot.remove(sectorToRemove);
                }
            }
        }

         */


// strzelanie do sektorów obliczonych jako potencjalne cele
        Sector currentTarget = null;
        int index = 0;
        String status = "";
        if (potentialTargets.size() > 0) {
            Sector tmpTarget = potentialTargets.get(0);
            potentialTargets.remove(0);
            int tmpX = tmpTarget.getCoordinateX();
            int tmpY = tmpTarget.getCoordinateY();
            for (Sector theSector : p1sectors) {
                if (theSector.getCoordinateX() == tmpX
                        && theSector.getCoordinateY() == tmpY) {
                    currentTarget = theSector;
                }
            }
        } else {



// losowanie sektora z listy do strzału
            Random random = new Random();
            index = random.nextInt(leftToShoot.size());
            //System.out.println("random sector index= " + index);
            currentTarget = p1sectors.get(index);
            status = currentTarget.getStatus();
        }


        if (status.equals("concealed_hull") || status.equals("concealed_origin")) {
// flagowanie na puste (dummy) i potencjalne cele
            int[] dx = {-1, 0, 1, 1, 1, 0, -1, -1};
            int[] dy = {-1, -1, -1, 0, 1, 1, 1, 0};
            boolean swapStatus = true;
            for (int n = 0; n < 8; n ++) {
                int newX = currentTarget.getCoordinateX() + dx[n];
                int newY = currentTarget.getCoordinateY() + dy[n];
                if (newX < 0 || newY < 0 || newX > 9 || newY > 9) {
                    break;
                }
                boolean makeAbreak = false;
                for (Sector sector : p1sectors) {
                    if (sector.getCoordinateX() == newX
                            && sector.getCoordinateY() == newY
                            && sector.getStatus().equals("exposed_clear")) {
                            makeAbreak = true;
                    }
                }
                if (makeAbreak) {
                    break;
                }
                Sector flagged = new Sector(5, newX, newY);
                if (swapStatus) {
                    dummies.add(flagged);
                } else {
                    potentialTargets.add(flagged);
                }
                swapStatus = !swapStatus;
            }
        }
// dążę do tego by nie używać listy "dummy" zamiast tego nadawać
// odpowiedni status na liście "leftToShoot".
// (ale w sumie listę dummy można zostawić jako produkt kontrolny)
        return p1sectors.get(index);
    }

// w tej grze ta funkcja używana jest tylko przez komputer, dlatego funkcja
// usuwa zawsze z listy "leftToShoot"
    void removeSector(Sector sector) {
        int x = sector.getCoordinateX();
        int y = sector.getCoordinateY();
        for (Sector sectorToRemove : leftToShoot) {
            if (sectorToRemove.getCoordinateX() == x
                    && sectorToRemove.getCoordinateY() == y) {
                leftToShoot.remove(sectorToRemove);
            }
        }
    }

    int evaluate() {
        score1 = score2 = 0;
        for (Sector s2 : p2sectors) {
            if (s2.getStatus().equals("exposed_hull") || s2.getStatus().equals("exposed_origin")) {
                score1 ++;
            }
        }
        for (Sector s1 : p1sectors) {
            if (s1.getStatus().equals("exposed_hull") || s1.getStatus().equals("exposed_origin")) {
                score2 ++;
            }
        }
        System.out.println("score1= " + score1 + "; score2= " + score2);
        if (score1 >= 20 || score2 >= 20) {
            if (score1 > score2) {
                return 1;
            }
            if (score2 > score1) {
                return 2;
            }
        } return 0;
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

    // funkcja do testów
    void listSectors() {
        for (int i = 0; i < 100; i ++) {
            String s1 = p1sectors.get(i).toString();
            String s2 = p2sectors.get(i).toString();
            System.out.println(s1 + "|||" + s2);
        }
    }
}
