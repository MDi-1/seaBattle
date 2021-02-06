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
import java.util.LinkedList;
import java.util.List;

public class Execlass extends Application{
    private final Image field = new Image("file:src/main/resources/field.png");
    private final Image x1unit = new Image("file:src/main/resources/unit1.png");
    private final Image x2unit = new Image("file:src/main/resources/unit2.png");
    private final Image x3unit = new Image("file:src/main/resources/unit3v2.png");
    private final Image x4unit = new Image("file:src/main/resources/unit4.png");
    private final Image splash = new Image("file:src/main/resources/splash.png");
    private final Image inTarget = new Image("file:src/main/resources/cross.png");
    private final Image proximity = new Image("file:src/main/resources/proximity.png");
    private final Label label1 = new Label("label1 label1 label1");
    private final Label label2 = new Label("label2 label2 label2");
    private GridPane grid1 = new GridPane();
    private GridPane grid2 = new GridPane();
    private GridPane grid3 = new GridPane();
    private Button starterBtn = new Button("START GAME");
    private Button p1beginBtn = new Button("PLAYER 1  DEPLOY UNITS");
    private Button p2beginBtn = new Button("PLAYER 1 DEPLOYMENT FINISHED");
    private Button activateAiBtn = new Button("PLAY AGAINST COMPUTER");
    private Button enterButton = new Button("ENTER THE BATTLE");
    private Button rotateBtn  = new Button("ROTATE");
    private Rotate rotate = new Rotate();
    private List<Integer> hullSectors = new ArrayList<>();
    private Pane[] sidePaneArray = new Pane[4];
    private Label[] unitCounter = new Label[4];
    private int player2prepare = 1;
    private boolean visualCheck = false;
    private Process process = new Process();

    void clearArea() {
        grid1.getChildren().clear();
//        sidePane1.getChildren().clear();
//        sidePane2.getChildren().clear();
//        sidePane3.getChildren().clear();
//        sidePane4.getChildren().clear();
        grid3.getChildren().clear();
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
                break;
            case 1:
                grid3.add(rotateBtn, 1, 8, 3, 1);
                grid2.add(p2beginBtn, 1, 4, 8, 1);
                grid1.getChildren().remove(p1beginBtn);
                prepareFleet();
                player2prepare = 1;
                break;
            case 2:
                prepareFleet();
                grid3.add(rotateBtn, 1, 8, 3, 1);
                grid2.getChildren().removeAll();
                break;
            case 3:

        }
    }

    void prepareFleet() {
        process.createUnits();
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
            sidePaneArray[idx -1].setOnMouseClicked(e -> pick(finalShipName));
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
        for (Ship ship : process.fleet) {
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
    void placeShipImg(Sector sector, Ship exeShip) {
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
            grid1.add(imageview, x - offset, y, 1, unitSize);
        } else {
            grid1.add(imageview, x, y - offset, 1, unitSize);
        }
    }

    void placeAttributes(Sector sector, Ship exeShip) {
        int x = sector.getCoordinateX();
        int y = sector.getCoordinateY();
        label1.setText("unit deployed");
        process.placeUnit(x, y);
        process.setupProximity(sector, exeShip);
        process.alignHull(sector, exeShip, true);
    }

    public void showArea(List<Sector> processedSectors) {
        for (Sector readSector : processedSectors) {
            String status = readSector.getStatus();
            int x = readSector.getCoordinateX();
            int y = readSector.getCoordinateY();

            if (status != null) {
                if (process.getGamestate() < 3 && status.equals("proximity")) {
                    ImageView prox = new ImageView(proximity);
                    grid1.add(prox, x, y);
                }
                if (process.getGamestate() > 2 && status.equals("hull")) {
                    ImageView hull = new ImageView(inTarget);
                    grid1.add(hull, x, y);
                }
                if (visualCheck && status.equals("origin")) {
                    for (Ship revealedShip : process.getP1fleet()) {
                        if (x == revealedShip.getLocationX() && y == revealedShip.getLocationY()) {
                            placeShipImg(readSector, revealedShip);
                        }
                    }
                }
            }
        }
    }

    void place(Sector placementSector, Ship placedShip) {
        placeShipImg(placementSector, placedShip);
        placeAttributes(placementSector, placedShip);
        showArea(process.getP1sectors());
        updateUnitCounter();
    }

    void player2preparation() {
        player2prepare = player2prepare + 1;
    }

    void fire(Sector sector) {
        // tu powinna być animacja strzału, ale nie będzie bo nie ma czasu.
        String status = sector.getStatus();
        sector.setStatus("hit");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

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
        grid1.setGridLinesVisible(true);
        grid2.setGridLinesVisible(true);
        grid3.setGridLinesVisible(true);

        Pane[] paneArray = new Pane[100];
        int index = 0;
        for (Sector sector : process.getP1sectors()) {
            paneArray[index] = new Pane();
            grid1.add(paneArray[index], sector.getCoordinateX(), sector.getCoordinateY());
            int j = index;
            paneArray[index].setOnMouseEntered(e -> {
                if (process.getUnitInProcess() != null) {
//                    System.out.println("--------- entering sector");
                    process.alignHull(sector, process.getUnitInProcess(), false);
//                    System.out.println("sector restriction: " + process.isPlacementAllowed());
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
                        if ((j + h) >= 0 && (j + h) < 100) {
                            paneArray[j + h].getChildren().add(rectangle);
                        }
                    }
                }
            });
            paneArray[index].setOnMouseExited(e -> {
                if (process.getUnitInProcess() != null) {
                    for (int h : hullSectors) {
                        if ((j + h) >= 0 && (j + h) < 100) {
                            paneArray[j + h].getChildren().removeAll(paneArray[j + h].getChildren());
                        }
                    }
                }
                process.setPlacementAllowed(true);
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
                if (process.getGamestate() == 3) {
                    fire(sector);
                }
            });
            index++;
        }
        // FIXME - podczas ustawiania jednostek zostają zielone kwadraty, a nie powinny.

        // czy da się napisać tak by lambda podała "e" do pick() ?

        /*
        todo - odkomnetować to później
        for (Sector sector2p : process.getP2sectors()) {
            Pane sectorPane2 = new Pane();
            grid2.add(sectorPane2, sector2p.getCoordinateX(), sector2p.getCoordinateY());
            sectorPane2.setOnMouseClicked(e -> {
                if (process.getGamestate() == 2) {
                    place(sector2p);
                }
                if (process.getGamestate() == 3) {
                    fire(sector2p);
                }
            }   );
        }
         */

        label1.setPadding(new Insets(0, 50, 0, 120));
        label1.setFont(new Font("Arial", 32));
        label1.setTextFill(Color.web("#FFF"));
        label1.setTextAlignment(TextAlignment.LEFT);
        label2.setPadding(new Insets(0, 120, 0, 100));
        label2.setFont(new Font("Arial", 32));
        label2.setTextFill(Color.web("#FFF"));
        label2.setTextAlignment(TextAlignment.JUSTIFY);
        activateAiBtn.setFont(new Font("Arial", 20));
        starterBtn.setFont(new Font("Arial", 20));
        p1beginBtn.setFont(new Font("Arial", 20));
        p2beginBtn.setFont(new Font("Arial", 20));
        rotateBtn.setFont( new Font("Arial", 20));
        grid3.add(starterBtn, 0, 4, 4, 1);

        starterBtn.setOnAction(e -> buttonActions());
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
            grid1.add(enterButton, 2, 6, 7, 1);
        });
        rotateBtn.setOnAction(e -> {
            hullSectors.clear();
            int dir = process.rotateUnit();
            label2.setText("unit's heading: " + dir);
        });
        enterButton.setFont(new Font("Arial", 20));
        enterButton.setOnAction(e -> {
            visualCheck = true;
            process.autoDeployAll();
            showArea(process.getP1sectors());
            updateUnitCounter();
            visualCheck = false;
            buttonActions();
        });

        Button clearButton = new Button("clear");
        clearButton.setFont(new Font("Arial", 20));
        grid2.add(clearButton, 4, 8, 4, 1);
        clearButton.setOnAction(e -> clearArea());
        Button extraButton1 = new Button("refresh");
        extraButton1.setFont(new Font("Arial", 20));
        grid2.add(extraButton1, 0, 8, 4, 1);
        extraButton1.setOnAction(e -> {
//            process.setGamestate(7);
            showArea(process.getP1sectors());
        });
        Button autoButton = new Button("auto deploy");
        autoButton.setFont(new Font("Arial", 20));
        grid2.add(autoButton, 0, 0, 4, 1);
        autoButton.setOnAction(e -> {
//            process.setGamestate(7);
            visualCheck = true;
            process.autoDeployAll();
            showArea(process.getP1sectors());
            updateUnitCounter();
            visualCheck = false;
        });
        Button resetButton = new Button("reset deployment");
        resetButton.setFont(new Font("Arial", 20));
        grid2.add(resetButton, 0, 2, 4, 1);
        resetButton.setOnAction(e -> {
            process = new Process();
            clearArea();
            showArea(process.getP1sectors());
            updateUnitCounter();
        });
        Button autoStepDeplBtn = new Button("auto deploy step by step");
        autoStepDeplBtn.setFont(new Font("Arial", 20));
        grid2.add(autoStepDeplBtn, 6, 0, 4, 1);
        autoStepDeplBtn.setOnAction(e -> {
            System.out.println("---- auto deployment once ----");
//            process.setGamestate(7);
            process.autoDeploySingleUnit();
            showArea(process.getP1sectors());
            updateUnitCounter();
        });

        grid0.add(grid1, 0, 0);
        grid0.add(grid2, 1, 0);
        grid0.add(grid3, 2, 0);
        grid0.add(label1, 0, 1);
        grid0.add(label2, 1, 1);

        Scene scene = new Scene(grid0, 1400, 600, Color.BLACK);
        primaryStage.setTitle("seaBattle");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
// task - na koniec pousuwać wszystkie niepotrzebne "System.out.print"