package hr.fer.zemris.graph;

import hr.fer.zemris.graph.layout.ForceDirectedLayout;
import hr.fer.zemris.util.GraphLoader;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Demo4Grid extends Application {

    private static final double WIDTH = 1200;
    private static final double HEIGHT = 700;

    private static final String PATH = "grid10.json";
    //	private static final String PATH = "clique5.json";
    //	private static final String PATH = "honeycomb2.json";

    @Override
    public void start(Stage primaryStage) throws Exception {

        //		GridGenerator generator = new GridGenerator();
        //		Graph graph = generator.generateGraph();

        GraphLoader loader = new GraphLoader(PATH);
        Graph graph = loader.getGraph();

        //		graph.getNodes().forEach(node -> node.setFill(Paint.valueOf("#ff3333")));
        //		graph.getEdges().forEach(node -> node.setFill(Paint.valueOf("#000000")));

        ForceDirectedLayout fda = new ForceDirectedLayout(graph, WIDTH, HEIGHT);
        Scene scene = new Scene(fda, WIDTH, HEIGHT);

        primaryStage.setScene(scene);
        primaryStage.show();

        //		fda.setScaleX(0.1);
        //		fda.setScaleY(0.1);

        Timeline gameLoop = new Timeline(new KeyFrame(Duration.millis(30), e -> {
            fda.run(20);
        }));
        gameLoop.setCycleCount(Animation.INDEFINITE);
        gameLoop.play();

        scene.setOnMouseClicked(e -> fda.run(1));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
