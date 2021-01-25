package org.example.seabattle;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class Execlass extends Application{
    private final Image field = new Image("file:src/main/resources/field.png");
    private final Image x1unit = new Image("file:src/main/resources/unit1.png");
    private final Image x2unit = new Image("file:src/main/resources/unit2.png");
    private final Image x3unit = new Image("file:src/main/resources/unit3.png");
    private final Image x4unit = new Image("file:src/main/resources/unit4.png");
    private final FlowPane board = new FlowPane();
    private GridPane grid1 = new GridPane();
    private GridPane grid2 = new GridPane();
    private GridPane grid3 = new GridPane();
    private final Process process = new Process();

    public static void main(String[] args) {
        launch(args);
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

    @Override
    public void start(Stage primaryStage) throws Exception {
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(
                field, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background background = new Background(backgroundImage);

        board.setAlignment(Pos.TOP_LEFT);
        board.setBackground(background);
        board.setPadding(new Insets(72, 50, 50, 77));
        board.setHgap(92);
        board.setVgap(20);

        grid1.setAlignment(Pos.TOP_LEFT);
        //grid1.setPadding(new Insets(21, 25, 25, 27));
        grid2.setAlignment(Pos.TOP_LEFT);
        //grid2.setPadding(new Insets(1, 1, 1, 1));
        grid3.setAlignment(Pos.TOP_LEFT);
        grid3.setPadding(new Insets(21, 25, 25, 27));

        ColumnConstraints cConstr = new ColumnConstraints(46);
        RowConstraints rowConstr = new RowConstraints(46);
        for (int i = 0; i < 4; i ++) {
            grid3.getColumnConstraints().add(cConstr);
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
            sectorPane1.setOnMouseClicked(e -> { if (process.getGamestate() == 0) placeUnit(sector1p); }
            );
        }       // task - przemyśleć później atrybut "gamestate"
        for (Sector sector2p : process.getP2area()) {
            Pane sectorPane2 = new Pane();
            grid2.add(sectorPane2, sector2p.getCoordinateY(), sector2p.getCoordinateX());
            sectorPane2.setOnMouseClicked(e -> { if (process.getGamestate() == 0) placeUnit(sector2p); }
            );
        }

        Rotate rotate = new Rotate();
        rotate.setPivotX(23);
        rotate.setPivotY(23);
        rotate.setAngle(90);

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
        grid3.add(carrierIcon, 2, 6, 4, 2);
        grid3.add(cruiserIcon, 2, 4, 3, 1);
        grid3.add(submarineIcon, 2, 2, 2, 1);
        grid3.add(helicoptrIcon, 2, 0);

        board.getChildren().add(grid1);
        board.getChildren().add(grid2);
        board.getChildren().add(grid3);
        process.createUnits();

        Scene scene = new Scene(board, 1400, 600, Color.BLACK);
        primaryStage.setTitle("seaBattle");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
