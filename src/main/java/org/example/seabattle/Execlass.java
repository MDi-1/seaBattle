package org.example.seabattle;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
    private final Process process = new Process();

    public static void main(String[] args) {
        launch(args);
    }

    void pick(String type) {
        label.setText(type);

    }

    // później tutaj można dać łapanie wyjątku "out of bounds" gdy wywołana lista nie jest jeszcze utworzona albo
    // wywołana pickUnit() w switchu nie działa z innych powodów
    void placeUnit(Sector sector /*, Ship ship, ImageView unitIcon */ ) {
        switch (process.pickUnit().getType().substring(0, 3)) {
            case "sub":
                break;
            case "hel":
                ImageView heliIcon = new ImageView(x1unit);
                grid1.add(heliIcon, sector.getCoordinateY(), sector.getCoordinateX());
                break;
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
        //board.setHgap(92);
        board.setVgap(20);

        grid1.setAlignment(Pos.TOP_LEFT);
        //grid1.setPadding(new Insets(21, 25, 25, 27));
        grid2.setAlignment(Pos.TOP_LEFT);
        grid2.setPadding(new Insets(0, 0, 0, 92));
        grid3.setAlignment(Pos.TOP_LEFT);
        grid3.setPadding(new Insets(0, 0, 0, 46));
        txtBox.setAlignment(Pos.TOP_CENTER);
        txtBox.setPadding(new Insets(0, 0, 0, 0));

        ColumnConstraints cConstr = new ColumnConstraints(46);
        RowConstraints rowConstr = new RowConstraints(46);
        for (int i = 0; i < 5; i ++) {
            grid3.getColumnConstraints().add(cConstr);
        }
        for (int i = 0; i < 8; i ++) {
            grid3.getRowConstraints().add(rowConstr);
        }
        int sidePaneX = 1;
        int sidePaneY = 1;
        for (int i = 0; i < 40; i ++) {
            Pane sidePane = new Pane();
            grid3.add(sidePane, sidePaneY, sidePaneX);
            sidePaneX ++;
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
                        if (process.getGamestate() == 0) placeUnit(sector1p);
                        if (process.getGamestate() == 1) shoot(sector1p);
                    }
            );
        }       // task - przemyśleć później atrybut "gamestate"
        for (Sector sector2p : process.getP2area()) {
            Pane sectorPane2 = new Pane();
            sectorPane2.setOnMouseClicked(e -> {
                        if (process.getGamestate() == 0) placeUnit(sector2p);
                        if (process.getGamestate() == 2) shoot(sector2p);
                    }
            );
        }

        Rotate rotate = new Rotate();
        rotate.setPivotX(23);
        rotate.setPivotY(23);
        rotate.setAngle(270);

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

        ImageView carrierIcon = new ImageView(x4unit);
        ImageView cruiserIcon = new ImageView(x3unit);
        ImageView submarineIcon = new ImageView(x2unit);
        ImageView helicoptrIcon = new ImageView(x1unit);
        carrierIcon.getTransforms().add(rotate);
        cruiserIcon.getTransforms().add(rotate);
        submarineIcon.getTransforms().add(rotate);
        helicoptrIcon.getTransforms().add(rotate);
        grid3.add(carrierIcon, 0, 0, 4, 2);
        grid3.add(cruiserIcon, 0, 2, 4, 1);
        grid3.add(submarineIcon, 0, 4, 1, 1);
        grid3.add(helicoptrIcon, 0, 6);
        // czy da się napisać tak by lambda podała "e" do pick() ?
        carrierIcon.setOnMouseClicked(e -> { if (process.getGamestate() == 0) pick("carrier"); } );
        cruiserIcon.setOnMouseClicked(e -> { if (process.getGamestate() == 0) pick("cruiser"); } );
        submarineIcon.setOnMouseClicked(e -> { if (process.getGamestate() == 0) pick("submarine"); } );
        helicoptrIcon.setOnMouseClicked(e -> { if (process.getGamestate() == 0) pick("helicopter"); } );

        label.setFont(new Font("Arial", 24));
        label.setTextFill(Color.web("#FFF"));
        label.setTextAlignment(TextAlignment.CENTER);

        board.getChildren().add(grid1);
        board.getChildren().add(grid2);
        board.getChildren().add(grid3);
        board.getChildren().add(txtBox);
        process.createUnits();

        Scene scene = new Scene(board, 1400, 600, Color.BLACK);
        primaryStage.setTitle("seaBattle");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
