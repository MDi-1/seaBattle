package org.example.seabattle;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class Execlass extends Application{
    // później przenieść pliki png gdzieś gdzie będą dostępne po kompilacji (poza src)
    private final Image field = new Image("file:src/main/resources/field.png");
    private final Image x1unit = new Image("file:src/main/resources/unit1.png");
    private final Image x2unit = new Image("file:src/main/resources/unit2.png");
    private final Image x3unit = new Image("file:src/main/resources/unit3v2.png");
    private final Image x4unit = new Image("file:src/main/resources/unit4.png");
    private final Image splash = new Image("file:src/main/resources/splash.png");
    private final Image targetHit = new Image("file:src/main/resources/cross.png");
    private final Image proximity = new Image("file:src/main/resources/proximity.png");
    private final Label label1 = new Label(" [ SEA BATTLE ] ");
    private final Label label2 = new Label("");
    private GridPane grid1 = new GridPane();
    private GridPane grid2 = new GridPane();
    private GridPane grid3 = new GridPane();
    private Button starterBtn = new Button("START GAME");
    private Button p1beginBtn = new Button("PLAYER 1  DEPLOY UNITS");
    private Button p2beginBtn = new Button("PLAYER 1 DEPLOYMENT FINISHED");
    private Button activateAiBtn = new Button("PLAY AGAINST COMPUTER");
    private Button enterButton = new Button("ENTER THE BATTLE");
    private Button rotateBtn  = new Button("ROTATE");
    Button clearButton = new Button("clear");
    Button refreshButton = new Button("refresh");
    private Rotate rotate = new Rotate();
    private List<Integer> hullSectors = new ArrayList<>();
    private Pane[] sidePaneArray = new Pane[4];
    Pane[] paneArrayP1 = new Pane[100];
    Pane[] paneArrayP2 = new Pane[100];
    private final Label[] unitCounter = new Label[4];
    private int player2prepare = 1;
    private boolean visualCheck = true;
    private Process process = new Process();
    private Label scCount1 = new Label();
    private Label scCount2 = new Label();
    Service service = new Service();


    void clearArea() {
        grid1.getChildren().clear();
        if (process.getGamestate() > 1) {
            grid2.getChildren().clear();
        }
    }

    void buttonActions() {
        int i = process.getGamestate() + 1;
        process.setGamestate(i);
        int state = process.getGamestate();
        System.out.println("gamestate set to >> " + state);
        switch (state) {
            case 0:
                grid1.add(p1beginBtn, 2, 4, 7, 1);
                grid3.getChildren().remove(starterBtn);
                label1.setText("");
                label2.setText("");
                break;
            case 1:
                grid3.add(rotateBtn, 1, 8, 3, 1);
                grid2.add(p2beginBtn, 1, 4, 8, 1);
                label1.setText("click on the right panel");
                label2.setText("to pick unit");
                grid1.getChildren().remove(p1beginBtn);
                prepareFleet();
                player2prepare = 1;
                break;
            case 2:
                prepareFleet();
                grid3.add(rotateBtn, 1, 8, 3, 1);
                grid2.getChildren().clear();
                label1.setText("");
                label2.setText("");
                visualCheck = false;
                break;
            case 3:
                visualCheck = true;
                grid1.getChildren().clear();
                grid2.getChildren().clear();
                grid3.getChildren().remove(enterButton);
                grid3.add(scCount1, 0, 3, 5, 1);
                grid3.add(scCount2, 0, 5, 5, 1);
                process.rebuildSectors();
                createShootablePanes();
                grid3.add(starterBtn, 0, 0, 5, 1);
                starterBtn.setText("FINISH THE BATTLE");
        }
    }

    void prepareFleet() {
        process.createTmpFleet();
        for (int idx = 4; idx > 0; idx--) {
            Image imgName = null;
            String shipName = "nothing";
            switch (idx) {
                case 4:
                    imgName = x4unit;
                    shipName = "carrier";
                    break;
                case 3:
                    imgName = x3unit;
                    shipName = "cruiser";
                    break;
                case 2:
                    imgName = x2unit;
                    shipName = "submarine";
                    break;
                case 1:
                    imgName = x1unit;
                    shipName = "helicopter";
            }
            ImageView view = new ImageView(imgName);
            view.getTransforms().add(rotate);
            String finalShipName = shipName;
            sidePaneArray[idx -1] = new Pane();
            sidePaneArray[idx -1].getChildren().add(view);
            grid3.add(sidePaneArray[idx -1], 0, (2 * idx) - 2);
            sidePaneArray[idx -1].setOnMouseClicked(e -> {
                pick(finalShipName);
                hullSectors.clear();
            });
            unitCounter[idx -1] = new Label("x?");
            unitCounter[idx -1].setFont(new Font("Arial", 20));
            unitCounter[idx -1].setTextFill(Color.web("#FFF"));
            unitCounter[idx -1].setTextAlignment(TextAlignment.RIGHT);
            grid3.add(unitCounter[idx -1], 4, (2 * idx) - 2);
        }
        updateUnitCounter();
    }

    void updateUnitCounter() {
        int helQuantity = 0;
        int subQuantity = 0;
        int cruQuantity = 0;
        int carQuantity = 0;
        for (Ship ship : process.getFleet()) {
            switch (ship.getShipSize()) {
                case 1:
                    helQuantity ++;
                    break;
                case 2:
                    subQuantity ++;
                    break;
                case 3:
                    cruQuantity ++;
                    break;
                case 4:
                    carQuantity ++;
            }
        }
        unitCounter[0].setText("  x" + helQuantity);
        unitCounter[1].setText("  x" + subQuantity);
        unitCounter[2].setText("  x" + cruQuantity);
        unitCounter[3].setText("  x" + carQuantity);
    }

    void pick(String fullType) {
        label1.setText("deploy " + fullType);
        process.pickUnit(fullType);
        if (process.getUnitInProcess() != null) {
            label2.setText("unit's heading: " + process.getUnitInProcess().getHeading());
        }
    }

    // później tutaj można dać łapanie wyjątku "out of bounds" gdy wywołana lista nie jest jeszcze utworzona albo
    // wywołana pickUnit() w switch-u nie działa z innych powodów
    void placeShipImg(Sector sector, Ship exeShip, GridPane grid) {
        int x = sector.getCoordinateX();
        int y = sector.getCoordinateY();
        int unitSize = exeShip.getShipSize();
        int offset = (unitSize + 1) / 4;
        int heading = exeShip.getHeading();
        if (((heading == 270) && (x - offset < 0 || y < 0)) || (heading == 0) && (x < 0 || (y - offset < 0))) {
            return; // zapobieganie NPE
        }
        hullSectors.clear();
        Image imageName = null;
        switch (unitSize) {
            case 4:  imageName = x4unit;  break;
            case 3:  imageName = x3unit;  break;
            case 2:  imageName = x2unit;  break;
            case 1:  imageName = x1unit;  break;
        }
        ImageView imageview = new ImageView(imageName);
        if (exeShip.getHeading() == 270) {
            imageview.getTransforms().add(rotate);
            grid.add(imageview, x - offset, y, 1, unitSize);
        } else {
            grid.add(imageview, x, y - offset, 1, unitSize);
        }
    }

    void placeAttributes(Sector sector, Ship exeShip) {
        int player = sector.getPlayer();
        int x = sector.getCoordinateX();
        int y = sector.getCoordinateY();
        label1.setText("unit deployed");
        process.placeUnit(x, y);
        process.setupProximity(sector, exeShip);
        if (player == 1) {
            process.alignHull(process.getP1sectors(), sector, exeShip, true);
        }
        if (player == 2) {
            process.alignHull(process.getP2sectors(), sector, exeShip, true);
        }
    }

    public void showArea(List<Sector> processedSectors, GridPane grid) {
        for (Sector readSector : processedSectors) {
            String status = readSector.getStatus();
            int player = readSector.getPlayer();
            int x = readSector.getCoordinateX();
            int y = readSector.getCoordinateY();
            List<Ship> exeFleet = null;

            switch (status) {
                case "proximity":
                    ImageView prox = new ImageView(proximity);
                    grid.add(prox, x, y);
                    break;
                case "exposed_clear":
                    ImageView waterSplash = new ImageView(splash);
                    grid.add(waterSplash, x, y);
                    break;
                case "exposed_hull":
                    ImageView cross = new ImageView(targetHit);
                    grid.add(cross, x, y);
                    break;

                case "exposed_origin":
                    if (player == 1) {
                        exeFleet = process.getP1fleet();
                    }
                    if (player == 2) {
                        exeFleet = process.getP2fleet();
                    }
                    for (Ship revealedShip : exeFleet) {
                        if (x == revealedShip.getLocationX() && y == revealedShip.getLocationY()) {
                            placeShipImg(readSector, revealedShip, grid);
                        }
                    }
                    ImageView hit = new ImageView(targetHit);
                    grid.add(hit, x, y);
                case "origin":
                case "concealed_origin": // te 3 ostatnie "case" zrobić tu porządek
                    if (visualCheck) {
                        if (player == 1) {
                            exeFleet = process.getP1fleet();
                        }
                        if (player == 2) {
                            exeFleet = process.getP2fleet();
                        }
                        for (Ship revealedShip : exeFleet) {
                            if (x == revealedShip.getLocationX() && y == revealedShip.getLocationY()) {
                                placeShipImg(readSector, revealedShip, grid);
                            }
                        }
                    } else {
                        ImageView hit1 = new ImageView(targetHit);
                        grid.add(hit1, x, y);
                    }
                default:
            }
        }
    }

    void place(Sector placementSector, Ship placedShip) {
        placeAttributes(placementSector, placedShip);
        if (placementSector.getPlayer() == 1) {
            placeShipImg(placementSector, placedShip, grid1);
            showArea(process.getP1sectors(), grid1);
        }
        if (placementSector.getPlayer() == 2) {
            placeShipImg(placementSector, placedShip, grid2);
            showArea(process.getP2sectors(), grid2);
        }
        updateUnitCounter();
    }

    void player2preparation() {
        player2prepare = player2prepare + 1;
    }

    void createClickablePanes(Pane[]paneArray, Sector sector, int index, GridPane grid) {
        paneArray[index] = new Pane();
        grid.add(paneArray[index], sector.getCoordinateX(), sector.getCoordinateY());
        paneArray[index].setOnMouseEntered(e -> {
            if (process.getUnitInProcess() != null) {
                process.alignHull(process.getP1sectors(), sector, process.getUnitInProcess(), false);
                int size = process.getUnitInProcess().getShipSize();
                int offset = (size + 1) / 4;
                int element, dir;
                if (process.getUnitInProcess().getHeading() == 270) {
                    dir = 10;
                } else {
                    dir = 1;
                }
                for (int n = -offset; n < size - offset; n ++) {
                    element = n * dir;
                    hullSectors.add(element);
                }
                for (int h : hullSectors) {
                    Rectangle rectangle = new Rectangle(30, 30, Color.TRANSPARENT);
                    rectangle.setX(8);
                    rectangle.setY(8);
                    if (process.isPlacementAllowed()) {
                        rectangle.setStroke(Color.valueOf("#00ff00"));
                    } else {
                        rectangle.setStroke(Color.valueOf("#ff0000"));
                    }
                    rectangle.setStrokeWidth(2);
                    if ((index + h) >= 0 && (index + h) < 100) {
                        paneArray[index + h].getChildren().add(rectangle);
                    }
                }
            }
        });
        paneArray[index].setOnMouseExited(e -> {
            if (process.getUnitInProcess() != null) {
                for (int h : hullSectors) {
                    if ((index + h) >= 0 && (index + h) < 100) {
                        paneArray[index + h].getChildren().removeAll(paneArray[index + h].getChildren());
                    }
                }
            }
            process.allowPlacement(true);
            });
        paneArray[index].setOnMouseClicked(e -> {
            if (process.getGamestate() == 1) {
                if (process.getUnitInProcess() != null) {
                    if (process.isPlacementAllowed()) {
                        place(sector, process.getUnitInProcess());
                    }
                } else {
                    label1.setText("pick the ship first");
                }
            }
        });
    }

    void createShootablePanes() {
//        System.out.println("f. createShootablePanes invoked");
        Rectangle reticle = new Rectangle(30, 30, Color.TRANSPARENT);
        reticle.setX(8);
        reticle.setY(8);
        reticle.setStrokeWidth(2);

        for (Sector sector : process.getP2sectors()) {
            Pane pane = new Pane();
            grid2.add(pane, sector.getCoordinateX(), sector.getCoordinateY());
            pane.setOnMouseEntered(e -> {
                    if (sector.getStatus().startsWith("exposed")) {
                        reticle.setStroke(Color.valueOf("#ff0000"));
                    } else {
                        reticle.setStroke(Color.valueOf("#ffff00"));
                        process.allowFire(true);
                    }
                    if (pane.getChildren().size() < 1) {
                        pane.getChildren().add(reticle);
                    }
            });
            pane.setOnMouseExited(e -> {
                pane.getChildren().removeAll();
                process.allowFire(false);
            });
            pane.setOnMouseClicked(e -> {
                if (process.isFireFree()) {
                    nextTurn(sector);
                }
            });
        }
    }

    boolean fire(Sector sector) {
        List<Sector> fleetHulls = null;
        GridPane grid = null;
        int areaToShootAt = sector.getPlayer();
        int x = sector.getCoordinateX();
        int y = sector.getCoordinateY();
        char a = (char) (y + 65);
        boolean unitHit = false;
        String result;
        if (areaToShootAt == 1) {
            grid = grid1;
            fleetHulls = process.getFleet1hulls();
        }
        if (areaToShootAt == 2) {
            grid = grid2;
            fleetHulls = process.getFleet2hulls();
        }
        String status = sector.getStatus();
        switch (status) {
            case "concealed_clear":
                result = "exposed_clear";
                sector.setStatus(result);
                ImageView missed = new ImageView(splash);
                grid.add(missed, x, y);
                break;
            case "concealed_hull":
                result = "exposed_hull";
                sector.setStatus(result);
                process.removeSector(fleetHulls, sector);
                ImageView shipHit = new ImageView(targetHit);
                grid.add(shipHit, x, y);
                unitHit = true;
                break;
            case "concealed_origin":
                result = "exposed_origin";
                sector.setStatus(result);
                process.removeSector(fleetHulls, sector);
                ImageView criticalHit = new ImageView(targetHit);
                grid.add(criticalHit, x, y);
                unitHit = true;
                break;
            default:
                return false;
        }
        if (areaToShootAt == 2) {
            if (unitHit) {
                label2.setText("You hit enemy unit at: " + a  + (x + 1));
            } else {
                label2.setText("");
            }
            label1.setText("");
        }
        if (areaToShootAt == 1) {
            if (unitHit) {
                label1.setText("Your unit was hit at: " + a  + (x + 1));
            }
            // setSectorInProcess attribute is used by computer player.
            // When 2 human players functionality is introduced this line has to be changed.
            process.setSectorInProcess(sector);
        } return unitHit;
    }

    void nextTurn(Sector sector) {
        boolean streak;
        int evaluation;
        boolean destroyed = false;
        streak = fire(sector);
        evaluation = process.evaluate();
        scCount1.setText("Player 1 score: " + process.getScore1());
        if (process.getFleet2hulls().size() < 1) {
            finish(evaluation);
        }
        if (streak)
            return;
        process.setGamestate(4);
        do {//gdy dodamy możliwość grania na 2 os.- dodać warunek do computerIsShooting()
            int sunkCheck = process.getSunkQuantity();
            int sunkAmount = process.defineSunk(1);
            process.setSunkQuantity(sunkAmount);
            if (sunkCheck != process.getSunkQuantity()) {
                destroyed = true;
            } // spróbować zebrać sunkCheck w zewn. funkcję
            // sunkCheck w tym miejscu działa na razie tylko dla komputera
            System.out.println("destroyed=" + destroyed);
            Sector targetSector = process.computerIsShooting(destroyed);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // test blok
//            int x = targetSector.getCoordinateX();
//            int y = targetSector.getCoordinateY();
//            char a = (char) (y + 65);
//            System.out.println(" !!! computer is shooting at " + a  + (x + 1));

            streak = fire(targetSector);
            evaluation = process.evaluate();
            scCount2.setText("Player 2 score: " + process.getScore2());
            if (process.getFleet1hulls().size() < 1) {
                finish(evaluation);
                return;
            }
        } while (streak);
        process.setGamestate(3);
    }

    void finish(int player) {
        process.setGamestate(5);
        grid2.getChildren().clear();
        visualCheck = true;
        showArea(process.getP1sectors(), grid1);
        showArea(process.getP2sectors(), grid2);
        grid3.getChildren().remove(starterBtn);
        label1.setText("END OF GAME");
        if (player == 0) {
            label2.setText("The Draw");
        } else {
            label2.setText("Player " + player + " wins.");
        }
    }

    @Override
    public void start(Stage primaryStage) /* throws Exception */ {

        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(
                field, BackgroundRepeat.REPEAT,BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background background = new Background(backgroundImage);

        GridPane grid0 = new GridPane();
        grid0.setAlignment(Pos.TOP_LEFT);
        grid0.setBackground(background);
        grid0.setPadding(new Insets(72, 0, 0, 77));

        grid1.setAlignment(Pos.TOP_LEFT);
        grid2.setAlignment(Pos.TOP_LEFT);
        grid2.setPadding(new Insets(0, 0, 0, 92));
        grid3.setAlignment(Pos.TOP_LEFT);
        grid3.setPadding(new Insets(0, 0, 0, 46));

        rotate.setPivotX(23);
        rotate.setPivotY(23);
        rotate.setAngle(270);

        // setting up the grid shapes
        ColumnConstraints cConstr = new ColumnConstraints(46);
        RowConstraints rowConstr = new RowConstraints(46);
        for (int i = 0; i < 5; i ++) {
            grid3.getColumnConstraints().add(cConstr);
        }
        for (int i = 0; i < 8; i ++) {
            grid3.getRowConstraints().add(rowConstr);
        }
        for (int i = 0; i < 10; i ++) {
            grid1.getColumnConstraints().add(cConstr);
            grid1.getRowConstraints().add(rowConstr);
            grid2.getColumnConstraints().add(cConstr);
            grid2.getRowConstraints().add(rowConstr);
        }
        // siatka do usunięcia później
        grid0.setGridLinesVisible(true);
        grid1.setGridLinesVisible(true);
        grid2.setGridLinesVisible(true);
        grid3.setGridLinesVisible(true);

        int index1 = 0;
        for (Sector sector : process.getP1sectors()) {
            createClickablePanes(paneArrayP1, sector, index1, grid1);
            index1++;
        }
        int index2 = 0;
        for (Sector sector : process.getP2sectors()) {
            createClickablePanes(paneArrayP2, sector, index2, grid2);
            index2++;
        }
        // FIXME - podczas ustawiania jednostek zostają zielone kwadraty, a nie powinny.
        // czy da się napisać tak by lambda podała "e" do pick() ?

        label1.setPadding(new Insets(0, 50, 50, 50));
        label1.setFont(new Font("Arial", 32));
        label1.setTextFill(Color.web("#FFF"));
        label1.setTextAlignment(TextAlignment.LEFT);
        label2.setPadding(new Insets(0, 50, 50, 140));
        label2.setFont(new Font("Arial", 32));
        label2.setTextFill(Color.web("#FFF"));
        label2.setTextAlignment(TextAlignment.JUSTIFY);
        scCount1.setFont(new Font("Arial", 24));
        scCount1.setTextFill(Color.web("#FFF"));
        scCount1.setTextAlignment(TextAlignment.LEFT);
        scCount2.setFont(new Font("Arial", 24));
        scCount2.setTextFill(Color.web("#FFF"));
        scCount2.setTextAlignment(TextAlignment.LEFT);
        activateAiBtn.setFont(new Font("Arial", 20));
        starterBtn.setFont(new Font("Arial", 20));
        p1beginBtn.setFont(new Font("Arial", 20));
        p2beginBtn.setFont(new Font("Arial", 20));
        rotateBtn.setFont( new Font("Arial", 20));
        grid3.add(starterBtn, 0, 4, 4, 1);

        Button serviceBtn1 = new Button("service1");
        serviceBtn1.setFont(new Font("Arial", 20));
        serviceBtn1.setOnAction(e -> {
            service.printout(process.getDummies(), "dummies");
            service.printout(process.getLeftToShoot(), "left2shoot");
        });
        Button serviceBtn2 = new Button("service2");
        serviceBtn2.setFont(new Font("Arial", 20));
        serviceBtn2.setOnAction(e -> {
            service.printout(process.getFleet1hulls(), "fleet1hulls");
            service.printout(process.getFleet2hulls(), "fleet2hulls");
        });


        starterBtn.setOnAction(e -> {
            if (process.getGamestate() == -1) {
                buttonActions();
            } else {
                finish(0);
            }
        });
        p1beginBtn.setOnAction(e -> buttonActions());
        p2beginBtn.setOnAction(e -> {
            grid1.getChildren().clear();
            grid3.getChildren().clear();
            p2beginBtn.setText("PLAYER 2  DEPLOY UNITS");
            if (player2prepare == 2) {
                buttonActions();
            } else {
                player2preparation();
                grid2.add(activateAiBtn, 1, 6, 7, 1);
            }
        });
        activateAiBtn.setOnAction(e -> {
            buttonActions();
            grid3.getChildren().clear();
            player2preparation();
            grid3.add(clearButton, 0, 2, 4, 1);
            grid3.add(refreshButton, 0, 4, 4, 1);
            grid3.add(serviceBtn1, 0, 7, 2, 1);
            grid3.add(serviceBtn2, 3, 7, 2, 1);
            grid3.add(enterButton, 0, 6, 5, 1);
            process.autoDeployAll(); //tu winien być argument którą flotę wstawić
        });
        rotateBtn.setOnAction(e -> {
            hullSectors.clear();
            int dir = process.rotateUnit();
            label2.setText("unit's heading: " + dir);
        });
        enterButton.setFont(new Font("Arial", 20));
        enterButton.setOnAction(e -> {
            buttonActions();
            showArea(process.getP1sectors(), grid1);
            visualCheck = false;
        });

        clearButton.setFont(new Font("Arial", 20));
        grid2.add(clearButton, 4, 8, 4, 1);
        clearButton.setOnAction(e -> clearArea());
        refreshButton.setFont(new Font("Arial", 20));
        grid2.add(refreshButton, 0, 8, 4, 1);
        refreshButton.setOnAction(e -> {
            visualCheck = true;
            showArea(process.getP1sectors(), grid1);
            showArea(process.getP2sectors(), grid2);
            visualCheck = false;
            service.printout(process.getFleet1hulls(), "p1 hulls");
            service.printout(process.getFleet2hulls(), "p2 hulls");
        });
        Button autoButton = new Button("auto deploy");
        autoButton.setFont(new Font("Arial", 20));
        grid2.add(autoButton, 0, 0, 4, 1);
        autoButton.setOnAction(e -> {
            visualCheck = true;
            process.autoDeployAll();
            if (process.getGamestate() == 1) {
                showArea(process.getP1sectors(), grid1);
            }
            if (process.getGamestate() == 2) {
                showArea(process.getP2sectors(), grid2);
            }
            updateUnitCounter();
            visualCheck = false;
        });
        Button resetButton = new Button("reset deployment");
        resetButton.setFont(new Font("Arial", 20));
        grid2.add(resetButton, 0, 2, 4, 1);
        resetButton.setOnAction(e -> {
            clearArea();
            showArea(process.getP1sectors(), grid1);
            showArea(process.getP2sectors(), grid2);
            updateUnitCounter();
        });
        Button autoStepDeplBtn = new Button("auto deploy step by step");
        autoStepDeplBtn.setFont(new Font("Arial", 20));
        grid2.add(autoStepDeplBtn, 6, 0, 4, 1);
        autoStepDeplBtn.setOnAction(e -> {
            process.autoDeploySingleUnit();
            if (process.getGamestate() == 1) {
                showArea(process.getP1sectors(), grid1);
            }
            if (process.getGamestate() == 2) {
                showArea(process.getP2sectors(), grid2);
            }
            updateUnitCounter();
        });

        grid0.add(grid1, 0, 0);
        grid0.add(grid2, 1, 0);
        grid0.add(grid3, 2, 0);
        grid0.add(label1, 0, 1);
        grid0.add(label2, 1, 1);

        ColumnConstraints constrX460 = new ColumnConstraints(460);
        RowConstraints constrY560 = new RowConstraints(560);
        grid0.getRowConstraints().add(constrY560);
        grid0.getColumnConstraints().add(constrX460);


        Scene scene = new Scene(grid0, 1400, 720, Color.BLACK);
        primaryStage.setTitle("seaBattle");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}