package hr.fer.zemris.graph.test;

import hr.fer.zemris.util.GraphLoader;
import java.util.Random;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class CirclesTest extends Application {

    private static final double WIDTH = 1200;
    private static final double HEIGHT = 700;

    private Random rand = new Random();

    @Override
    public void start(Stage primaryStage) throws Exception {

        Circle u = new Circle(100, 400, 20);
        u.setFill(Paint.valueOf("#383838"));

        Circle v = new Circle(700, 400, 20);
        v.setFill(Paint.valueOf("#383838"));

        Line edge = new Line();

        GraphLoader loader = new GraphLoader("miserables.json");

        double a = 0.5;
        double b = 4;
        double min = 1;
        double max = 17;

        double value = 1;

        double result = (b - a) * (value - min) / (max - min) + a;
        System.out.println(result);
        edge.setStrokeWidth(result);

        edge.startXProperty().bind(v.centerXProperty());
        edge.startYProperty().bind(v.centerYProperty());
        edge.endXProperty().bind(u.centerXProperty());
        edge.endYProperty().bind(u.centerYProperty());

        Pane pane = new Pane(edge, u, v);

        Scene scene = new Scene(pane, WIDTH, HEIGHT);

        scene.setOnZoom(e -> {
            System.out.println(e.getZoomFactor());
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
