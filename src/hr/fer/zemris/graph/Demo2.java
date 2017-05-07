package hr.fer.zemris.graph;

import hr.fer.zemris.graph.layout.FruchtermanReingoldLayout;
import hr.fer.zemris.util.GraphLoader;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Demo2 extends Application {

    private static final double WIDTH = 1200;
    private static final double HEIGHT = 700;

    private static final String PATH = "grid7.json";

    @Override
    public void start(Stage primaryStage) throws Exception {

        GraphLoader loader = new GraphLoader(PATH);
        Graph graph = loader.getGraph();

        graph.getEdges()
            .forEach(e -> e.setValue(e.getValue(), GraphLoader.getMinValue(),
                GraphLoader.getMaxValue()));

        FruchtermanReingoldLayout fda = new FruchtermanReingoldLayout(graph, WIDTH, HEIGHT);
        Scene scene = new Scene(fda, WIDTH, HEIGHT);

        primaryStage.setScene(scene);
        primaryStage.show();

        //		fda.setScaleX(0.04);
        //		fda.setScaleY(0.04);

        Timeline gameLoop = new Timeline(new KeyFrame(Duration.millis(50), e -> {
            fda.run(0.5);
        }));
        gameLoop.setCycleCount(Animation.INDEFINITE);
        gameLoop.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
