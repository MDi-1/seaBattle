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
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class Execlass extends Application{
    private final Image field = new Image("file:src/main/resources/field.png");
    private final Image x1unit = new Image("file:src/main/resources/unit1.png");
    private final Image x2unit = new Image("file:src/main/resources/unit2.png");
    private final Image x3unit = new Image("file:src/main/resources/unit3v2.png");
    private final Image x4unit = new Image("file:src/main/resources/unit4.png");
    private final Image splash = new Image("file:src/main/resources/splash.png");
    private final Image inTarget = new Image("file:src/main/resources/cross.png");
    private final FlowPane board = new FlowPane();
    private final Label label = new Label("label label label");
    private final HBox txtBox = new HBox(label);
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
    ImageView carrierIcon = new ImageView(x4unit);
    ImageView cruiserIcon = new ImageView(x3unit);
    ImageView submarineIcon = new ImageView(x2unit);
    ImageView helicoptrIcon = new ImageView(x1unit);
    private final Process process = new Process();

    public static void main(String[] args) {
        launch(args);
    }

    void buttonActions() {
        switch (process.getGamestate()) {
            case 0:
                grid3.getChildren().remove(starterBtn);
                grid1.add(p1beginBtn, 2, 4, 7, 1);
                process.setGamestate(1);                    // fixme - to powinno być w klasie Process
                break;
            case 1:
                process.createUnits();                      // fixme - to powinno być w klasie Process
                grid1.getChildren().remove(p1beginBtn);
                grid2.add(p2beginBtn, 2, 4, 7, 1);
                prepareFleet();
                break;
            case 3:
                process.setGamestate(3);                    // fixme - to powinno być w klasie Process
                process.createUnits();                      // fixme - to powinno być w klasie Process
                grid2.getChildren().remove(p2beginBtn);
                prepareFleet();
            default:                                        // coś tu trzeba wpisać
        }
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
        label.setText("deploy " + fullType);
        process.pickUnit(fullType);
    }

    // później tutaj można dać łapanie wyjątku "out of bounds" gdy wywołana lista nie jest jeszcze utworzona albo
    // wywołana pickUnit() w switchu nie działa z innych powodów
    void place(Sector sector) {
        if (process.getUnitInProcess() == null) {
            label.setText("pick the ship");
    // później zrobić tak by można było klikać do wyczerpania typów a nie tylko po jednym
            return;
        }
        switch (process.placeUnit()) {
            case "sub":
                break;
            case "hel":
                ImageView heliIcon = new ImageView(x1unit);
                grid1.add(heliIcon, sector.getCoordinateY(), sector.getCoordinateX());
                break;
            default:
        }
    }

    void shoot(Sector sector) {
        // tu powinna być animacja strzału, ale nie będzie bo nie ma czasu.
        String status = sector.getStatus();
        sector.setStatus("hit");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(
                field, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background background = new Background(backgroundImage);

        board.setAlignment(Pos.TOP_LEFT);
        board.setBackground(background);
        board.setPadding(new Insets(72, 0, 0, 77));
        board.setVgap(20);

        grid1.setAlignment(Pos.TOP_LEFT);
        grid2.setAlignment(Pos.TOP_LEFT);
        grid2.setPadding(new Insets(0, 0, 0, 92));
        grid3.setAlignment(Pos.TOP_LEFT);
        grid3.setPadding(new Insets(0, 0, 0, 46));
        txtBox.setAlignment(Pos.CENTER);
        txtBox.setPadding(new Insets(0, 0, 0, 0));

        Rotate rotate = new Rotate();
        rotate.setPivotX(23);
        rotate.setPivotY(23);
        rotate.setAngle(270);
        carrierIcon.getTransforms().add(rotate);
        cruiserIcon.getTransforms().add(rotate);
        submarineIcon.getTransforms().add(rotate);
        helicoptrIcon.getTransforms().add(rotate);

        // setting up the grids shapes and activating mouse clicks on them
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

        for (Sector sector1p : process.getP1area()) {
            Pane sectorPane1 = new Pane();
            grid1.add(sectorPane1, sector1p.getCoordinateY(), sector1p.getCoordinateX());
            sectorPane1.setOnMouseClicked(e -> {
                        if (process.getGamestate() == 1) {
                            place(sector1p);
                        }
                        if (process.getGamestate() == 3) {
                            shoot(sector1p);
                        }
                    }
            );
        }
        for (Sector sector2p : process.getP2area()) {
            Pane sectorPane2 = new Pane();
            sectorPane2.setOnMouseClicked(e -> {
                        if (process.getGamestate() == 2) {
                            place(sector2p);
                        }
                        if (process.getGamestate() == 3) {
                            shoot(sector2p);
                        }
                    }
            );
        }
        sidePane4.setOnMouseClicked(e -> pick("carrier"));
        sidePane3.setOnMouseClicked(e -> pick("cruiser"));
        sidePane2.setOnMouseClicked(e -> pick("submarine"));
        sidePane1.setOnMouseClicked(e -> pick("helicopter"));

        /*
        int sidePaneValueX = 0;
        int sidePaneValueY = 0;
        for (int i = 0; i < 40; i ++) {
            Pane sidePane = new Pane();
         prostokąt celem sprawdzenia
            Rectangle rectangle = new Rectangle();
            rectangle.setX(18);
            rectangle.setY(18);
            rectangle.setWidth(10);
            rectangle.setHeight(10);
            rectangle.setFill(Color.valueOf("#00ffff"));
            sidePane.getChildren().add(rectangle);
            if (sidePaneValueX > 4) {
                sidePaneValueX = 0;
                sidePaneValueY ++;
            }
            grid3.add(sidePane, sidePaneValueX, sidePaneValueY);
            sidePaneValueX ++;
        }
         */

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


//        grid3.add(carrierIcon, 0, 0, 4, 2);
//        grid3.add(cruiserIcon, 0, 2, 4, 1);
//        grid3.add(submarineIcon, 0, 4, 1, 1);
//        grid3.add(helicoptrIcon, 0, 6);
        // czy da się napisać tak by lambda podała "e" do pick() ?

        label.setPadding(new Insets(0, 120, 0, 120));
        label.setFont(new Font("Arial", 32));
        label.setTextFill(Color.web("#FFF"));
        label.setTextAlignment(TextAlignment.JUSTIFY);
        starterBtn.setFont(new Font("Arial", 20));
        p1beginBtn.setFont(new Font("Arial", 20));
        p2beginBtn.setFont(new Font("Arial", 20));

        grid3.add(starterBtn, 0, 4, 4, 1);

        starterBtn.setOnAction(e -> buttonActions());
        p1beginBtn.setOnAction(e -> buttonActions());
        p2beginBtn.setOnAction(e -> buttonActions());
        board.getChildren().add(grid1);
        board.getChildren().add(grid2);
        board.getChildren().add(grid3);
        board.getChildren().add(txtBox);

        Scene scene = new Scene(board, 1400, 600, Color.BLACK);
        primaryStage.setTitle("seaBattle");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
// task - na koniec pousuwać wszystkie niepotrzebne "System.out.print"