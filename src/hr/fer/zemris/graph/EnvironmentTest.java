package hr.fer.zemris.graph;

import hr.fer.zemris.graph.edge.Edge;
import hr.fer.zemris.graph.node.Node;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;

public class EnvironmentTest extends Application {

    private static final double WIDTH = 1200;
    private static final double HEIGHT = 700;

    private Random rand = new Random();

    private double pressedX = 0.0d;
    private double pressedY = 0.0d;

    @Override
    public void start(Stage primaryStage) throws Exception {

        Node n1 = new Node("ivica", 3);
        n1.setCenterX(100);
        n1.setCenterY(400);
        n1.setFill(Paint.valueOf("#aa3838"));

        Node n2 = new Node("braco", 3);
        n2.setCenterX(700);
        n2.setCenterY(400);
        n2.setFill(Paint.valueOf("#f83838"));

        Edge e1 = new Edge(n1, n2, 1);
        e1.setStrokeWidth(1);

        Node n3 = new Node("bracek", 3);
        n3.setCenterX(400);
        n3.setCenterY(200);
        n3.setFill(Paint.valueOf("#aaa888"));

        Edge e2 = new Edge(n1, n3, 1);
        e2.setStrokeWidth(1);

        Group root = new Group(e1, e2, n1, n2, n3);
        root.setStyle("-fx-background-color: #383838");

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setPannable(true);

        AnimatedZoomOperator zoomOperator = new AnimatedZoomOperator();

        Scene scene = new Scene(scrollPane, WIDTH, HEIGHT);

        // Listen to scroll events (similarly you could listen to a button click, slider, ...)
        scene.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                double zoomFactor = 1.5;
                if (event.getDeltaY() <= 0) {
                    // zoom out
                    zoomFactor = 1 / zoomFactor;
                }
                zoomOperator.zoom(root, zoomFactor, event.getSceneX(), event.getSceneY());
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public class AnimatedZoomOperator {

        private Timeline timeline;

        public AnimatedZoomOperator() {
            this.timeline = new Timeline(40);
        }

        public void zoom(javafx.scene.Node node, double factor, double x, double y) {
            // determine scale
            double oldScale = node.getScaleX();
            double scale = oldScale * factor;
            double f = (scale / oldScale) - 1;

            // determine offset that we will have to move the node
            Bounds bounds = node.localToScene(node.getBoundsInLocal());
            double dx = (x - (bounds.getWidth() / 2 + bounds.getMinX()));
            double dy = (y - (bounds.getHeight() / 2 + bounds.getMinY()));

            // timeline that scales and moves the node
            timeline.getKeyFrames().clear();
            timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(200),
                    new KeyValue(node.translateXProperty(), node.getTranslateX() - f * dx)),
                new KeyFrame(Duration.millis(200),
                    new KeyValue(node.translateYProperty(), node.getTranslateY() - f * dy)),
                new KeyFrame(Duration.millis(200), new KeyValue(node.scaleXProperty(), scale)),
                new KeyFrame(Duration.millis(200), new KeyValue(node.scaleYProperty(), scale))
            );
            timeline.play();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
