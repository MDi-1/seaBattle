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
    private final FlowPane board = new FlowPane();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(
                field, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background background = new Background(backgroundImage);

        board.setAlignment(Pos.TOP_LEFT);
        board.setBackground(background);
        board.setPadding(new Insets(50, 50, 50, 50));
        board.setHgap(20);
        board.setVgap(20);

        GridPane grid1 = new GridPane();
        GridPane grid2 = new GridPane();

        grid1.setAlignment(Pos.BOTTOM_RIGHT);
        grid1.setPadding(new Insets(21, 25, 25, 27));
        for (int i = 0; i < 10; i ++) {
            ColumnConstraints cConstr = new ColumnConstraints(46);
            RowConstraints rConstr = new RowConstraints(46);
            grid1.getColumnConstraints().add(cConstr);
            grid1.getRowConstraints().add(rConstr);
        }
        // do usunięcia później
        grid1.setGridLinesVisible(true);

        ImageView subIco = new ImageView(x2unit);
        ImageView heliIco = new ImageView(x1unit);
        Rotate rotate = new Rotate();
        rotate.setAngle(90);
        rotate.setPivotX(23);
        rotate.setPivotY(46);

        grid1.add(heliIco, 0, 0);
        grid1.add(subIco, 1, 1, 1, 2);
        for (int i = 2; i < 9; i ++) {
            ImageView subIc = new ImageView(x2unit);
            subIc.getTransforms().add(rotate);
            grid1.add(subIc, i * 2, 2, 2, 1);
        }
        board.getChildren().add(grid1);

        Scene scene = new Scene(board, 1400, 600, Color.BLACK);
        primaryStage.setTitle("seaBattle");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
