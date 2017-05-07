package hr.fer.zemris.graph.test;

import hr.fer.zemris.graph.layout.ForceDirectedLayout;
import hr.fer.zemris.util.GraphLoader;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Demo3 extends Application {

    private static final double WIDTH = 1200;
    private static final double HEIGHT = 700;

    private static final String PATH = "miserables.json";

    @Override
    public void start(Stage primaryStage) throws Exception {

        GraphLoader loader = new GraphLoader(PATH);
        Graph graph = loader.getGraph();

        ForceDirectedLayout fda = new ForceDirectedLayout(graph, WIDTH, HEIGHT);
        Scene scene = new Scene(fda, WIDTH, HEIGHT);

        primaryStage.setScene(scene);
        primaryStage.show();

        //		fda.setScaleX(0.1);
        //		fda.setScaleY(0.1);

        Timeline gameLoop = new Timeline(new KeyFrame(Duration.millis(30), e -> {
            fda.run(10);
        }));
        gameLoop.setCycleCount(Animation.INDEFINITE);
        gameLoop.play();

        scene.setOnMouseClicked(e -> fda.run(1));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
