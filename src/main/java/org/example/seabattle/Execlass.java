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
    private Pane sidePane1 = new Pane();
    private Pane sidePane2 = new Pane();
    private Pane sidePane3 = new Pane();
    private Pane sidePane4 = new Pane();
    Button starterBtn = new Button("START GAME");
    Button p1beginBtn = new Button("PLAYER 1  DEPLOY UNITS");
    Button p2beginBtn = new Button("PLAYER 2  DEPLOY UNITS");
    Button rotateBtn  = new Button("ROTATE");
    ImageView carrierIcon = new ImageView(x4unit);
    ImageView cruiserIcon = new ImageView(x3unit);
    ImageView submarineIcon = new ImageView(x2unit);
    ImageView helicoptrIcon = new ImageView(x1unit);
    Rotate rotate = new Rotate();
    boolean setupComplete = false;                      // to tylko do testów
    List<Integer> hullSectors = new ArrayList<>();
    private final Process process = new Process();

    public static void main(String[] args) {
        launch(args);
    }

    void clearArea() {
        grid1.getChildren().clear();
    }

    void changeState() {
        int i = process.getGamestate() + 1;
        process.setGamestate(i);
        if (process.getGamestate() == 2) {
            grid2.add(p2beginBtn, 2, 4, 7, 1);
        }
        // TASK - sektory mają być klikalne
    }

    void buttonActions() {
        switch (process.getGamestate()) {
            case 0:
                grid3.getChildren().remove(starterBtn);
                grid1.add(p1beginBtn, 2, 4, 7, 1);
                process.setGamestate(1);                  // to powinno być w klasie Process
                break;
            case 1:
                process.createUnits();                    // to powinno być w klasie Process
                grid1.getChildren().remove(p1beginBtn);
                grid3.add(rotateBtn, 1, 8, 3, 1);
                prepareFleet();
                break;
            case 3:
                process.setGamestate(3);                  // to powinno być w klasie Process
                process.createUnits();                    // to powinno być w klasie Process
                grid2.getChildren().remove(p2beginBtn);
                prepareFleet();
            default:                                      // coś tu trzeba wpisać
        }
    }

    void rotateShip() {
        hullSectors.clear();
        int dir = process.rotateUnit();
        label2.setText("unit's heading: " + dir);
    }

    void prepareFleet() {
        sidePane4.getChildren().add(carrierIcon);
        sidePane3.getChildren().add(cruiserIcon);
        sidePane2.getChildren().add(submarineIcon);
        sidePane1.getChildren().add(helicoptrIcon);
        grid3.add(sidePane4, 0, 0);
        grid3.add(sidePane3, 0, 2);
        grid3.add(sidePane2, 0, 4);
        grid3.add(sidePane1, 0, 6);
    }

    void pick(String fullType) {
        label1.setText("deploy " + fullType);
        process.pickUnit(fullType);
        if (process.getUnitInProcess() != null) {
            label2.setText("unit's heading: " + process.getUnitInProcess().getHeading());
        }
    }
    // później tutaj można dać łapanie wyjątku "out of bounds" gdy wywołana lista nie jest jeszcze utworzona albo
    // wywołana pickUnit() w switchu nie działa z innych powodów
    void place(Sector sector, Ship exeShip) {
        int x = sector.getCoordinateX();
        int y = sector.getCoordinateY();
        int unitSize = exeShip.getShipSize();
        int offset = (unitSize + 1) / 4;
        int heading = exeShip.getHeading();
        if (((heading == 270) && (x - offset < 0 || y < 0)) || (heading == 0) && (x < 0 || (y - offset < 0))) {
            return;
        }
        label1.setText("unit deployed");
        hullSectors.clear();
// blok wstawiania grafiki
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
        if (setupComplete) {
            return;
        }
// blok modyfikacji sektorów
        process.placeUnit(x, y);
        process.setupProximity(sector, exeShip);
        process.alignHull(sector, exeShip, true);
        if (process.fleet.size() < 1) changeState(); // gamestate zmienić gdzieś indziej
// blok wyświetlania
        showPerimeter();
    }

    public void showPerimeter() {
        for (Sector readSector : process.getP1sectors()) {
            String status = readSector.getStatus();
            int x = readSector.getCoordinateX();
            int y = readSector.getCoordinateY();

            if (status != null) {
                if (status.equals("proximity")) {
                    ImageView prox = new ImageView(proximity);
                    grid1.add(prox, x, y);
                }
                if (status.equals("hull")) {
                    ImageView hull = new ImageView(inTarget);
                    grid1.add(hull, x, y);
                }
                if (status.equals("origin")) {

                }
            }
        }
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

        Button extraButton = new Button("clear");
        extraButton.setFont(new Font("Arial", 20));
        extraButton.setOnAction(e -> clearArea());
        grid2.add(extraButton, 4, 6, 4, 1);
        Button extraButton1 = new Button("refresh");
        extraButton1.setFont(new Font("Arial", 20));
        extraButton1.setOnAction(e -> showPerimeter());
        grid2.add(extraButton1, 4, 8, 4, 1);

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
        carrierIcon.getTransforms().add(rotate);
        cruiserIcon.getTransforms().add(rotate);
        submarineIcon.getTransforms().add(rotate);
        helicoptrIcon.getTransforms().add(rotate);

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
        int k = 0;
        for (Sector sector : process.getP1sectors()) {
            paneArray[k] = new Pane();
            grid1.add(paneArray[k], sector.getCoordinateX(), sector.getCoordinateY());
            int j = k;
            paneArray[k].setOnMouseEntered(e -> {
                if (process.getUnitInProcess() != null) {
                    boolean a = process.alignHull(sector, process.getUnitInProcess(), false);
                    System.out.println(a);
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
                        if (a) {
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
            paneArray[k].setOnMouseExited(e -> {
                if (process.getUnitInProcess() != null) {
                    System.out.println("-------------");
                    for (int h : hullSectors) {
                        if ((j + h) >= 0 && (j + h) < 100) {
                            paneArray[j + h].getChildren().removeAll(paneArray[j + h].getChildren());
                        }
                    }
                }
            });
            paneArray[k].setOnMouseClicked(e -> {
                if (process.getGamestate() == 1) {
                    if (process.getUnitInProcess() != null) {
                        place(sector, process.getUnitInProcess());
                    } else {
                        label1.setText("pick the ship first");
                    }
                }
                if (process.getGamestate() == 3) {
                    fire(sector);
                }
            });
            k ++;
        }
        /*
        todo - UNCOMMENT THAT LATER
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

        sidePane4.setOnMouseClicked(e -> pick("carrier"));
        sidePane3.setOnMouseClicked(e -> pick("cruiser"));
        sidePane2.setOnMouseClicked(e -> pick("submarine"));
        sidePane1.setOnMouseClicked(e -> pick("helicopter"));

        // blok tymczasowy - sprawdzanie jak działa "colspan, rowspan"
        ImageView heliIco = new ImageView(x1unit);
        ImageView subIco = new ImageView(x2unit);
        grid1.add(heliIco, 0, 0);
        grid1.add(subIco, 1, 1, 1, 2);
        for (int i = 2; i < 4; i ++) {
            ImageView subIc = new ImageView(x2unit);
            grid1.add(subIc, i * 2, 2, 1, 2);
            subIc.getTransforms().add(rotate);
        }
        // koniec bloku tymczasowego

        // czy da się napisać tak by lambda podała "e" do pick() ?

        label1.setPadding(new Insets(0, 50, 0, 120));
        label1.setFont(new Font("Arial", 32));
        label1.setTextFill(Color.web("#FFF"));
        label1.setTextAlignment(TextAlignment.LEFT);
        label2.setPadding(new Insets(0, 120, 0, 100));
        label2.setFont(new Font("Arial", 32));
        label2.setTextFill(Color.web("#FFF"));
        label2.setTextAlignment(TextAlignment.JUSTIFY);
        starterBtn.setFont(new Font("Arial", 20));
        p1beginBtn.setFont(new Font("Arial", 20));
        p2beginBtn.setFont(new Font("Arial", 20));
        rotateBtn.setFont( new Font("Arial", 20));

        grid3.add(starterBtn, 0, 4, 4, 1);

        starterBtn.setOnAction(e -> buttonActions());
        p1beginBtn.setOnAction(e -> buttonActions());
        p2beginBtn.setOnAction(e -> buttonActions());
        rotateBtn.setOnAction(e ->  rotateShip());
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
}
// task - na koniec pousuwać wszystkie niepotrzebne "System.out.print"
// później spróbować zrealizować jakiś mechanizm wywołania funkcji jak w nt_inf.txt