package hr.fer.zemris.graph;

import hr.fer.zemris.graph.layout.ForceDirectedLayout;
import hr.fer.zemris.util.GraphLoader;
import java.io.IOException;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.util.Duration;

/** Constructs a scene with a pannable Map background. */
public class ZoomTest1 extends Application {

    private static final int WIDTH = 1200;
    private static final int HEIGHT = 700;

    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Drag the mouse to pan the map");

        //		GridGenerator generator = new GridGenerator();
        //		Graph graph = generator.generateGraph();

        GraphLoader loader = new GraphLoader("miserables.json");
        Graph graph = loader.getGraph();

        //		graph.getNodes().forEach(node -> node.setFill(Paint.valueOf("#ff3333")));
        //		graph.getEdges().forEach(node -> node.setFill(Paint.valueOf("#000000")));

        ForceDirectedLayout fda = new ForceDirectedLayout(graph, WIDTH, HEIGHT);

        //		fda.setScaleX(0.1);
        //		fda.setScaleY(0.1);

        Timeline gameLoop = new Timeline(new KeyFrame(Duration.millis(30), e -> {
            fda.run(10);
        }));
        gameLoop.setCycleCount(Animation.INDEFINITE);
        gameLoop.play();

        // wrap the scene contents in a pannable scroll pane.
        ScrollPane scroll = createScrollPane(fda);

        //		fda.setOnMouseClicked(e -> System.out.println("FDA LAYOUT"));

        StackPane stackPane = new StackPane(scroll);
        stackPane.setStyle("-fx-background-color: #383838");
        // show the scene.
        Scene scene = new Scene(fda, 1200, 700);
        scene.getStylesheets().addAll(getClass().getResource("boris.css").toExternalForm());
        stage.setScene(scene);
        //		stage.initStyle(StageStyle.UNDECORATED);
        //		stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        //		stage.setFullScreen(true);
        stage.show();

        // bind the preferred size of the scroll area to the size of the scene.
        scroll.prefViewportWidthProperty().bind(scene.widthProperty());
        scroll.prefViewportHeightProperty().bind(scene.heightProperty());

        // center the scroll contents.
        //		scroll.setHvalue(scroll.getHmin() + (scroll.getHmax() - scroll.getHmin()) / 2);
        //		scroll.setVvalue(scroll.getVmin() + (scroll.getVmax() - scroll.getVmin()) / 2);
    }

    private static final double MAX_SCALE = 2.5d;
    private static final double MIN_SCALE = .5d;

    private class ZoomHandler implements EventHandler<ScrollEvent> {

        private javafx.scene.Node nodeToZoom;

        private ZoomHandler(javafx.scene.Node nodeToZoom) {
            this.nodeToZoom = nodeToZoom;
        }

        @Override
        public void handle(ScrollEvent scrollEvent) {
            //			if (scrollEvent.isControlDown()) {
            final double scale = calculateScale(scrollEvent);
            nodeToZoom.setScaleX(scale);
            nodeToZoom.setScaleY(scale);
            scrollEvent.consume();
            //			}
        }

        private double calculateScale(ScrollEvent scrollEvent) {
            double scale = nodeToZoom.getScaleX() + scrollEvent.getDeltaY() / 100;

            if (scale <= MIN_SCALE) {
                scale = MIN_SCALE;
            } else if (scale >= MAX_SCALE) {
                scale = MAX_SCALE;
            }
            return scale;
        }
    }

    /** @return a ScrollPane which scrolls the layout. */
    private ScrollPane createScrollPane(Pane layout) {

        Group group = new Group(layout.getChildren());

        ScrollPane scroll = new ScrollPane(layout);

        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setPannable(true);

        scroll.setOnScroll(new ZoomHandler(layout));

        //		String css = getClass().getResource("boris.css").toExternalForm();
        //		scroll.setStyle(css);

        //		scroll.addEventFilter(ScrollEvent.ANY, event -> {
        //			event.consume();
        //		});

        //		scroll.setFitToWidth(true);
        //		scroll.setFitToHeight(true);

        return scroll;
    }

    public class ForceDirectedScrollPane extends ScrollPane {

        private static final double MIN_SCALE = 0.1d;
        private static final double MAX_SCALE = 10.0d;

        private DoubleProperty scale = new SimpleDoubleProperty(1.0);

        private Timeline timeline = new Timeline(60);

        public ForceDirectedScrollPane(Pane layout) {
            super(layout);

            scaleXProperty().bind(scale);
            scaleYProperty().bind(scale);

            layout.setOnScroll(event -> {

                //				double scaleValue = getScaleX();
                double scaleValue =
                    scale.getValue();// currently we only use Y, same value is used for X
                double oldScale = scaleValue;

                scaleValue *= Math.pow(1.01, event.getDeltaY());

                if (scaleValue <= MIN_SCALE) {
                    scaleValue = MIN_SCALE;
                } else if (scaleValue >= MAX_SCALE) {
                    scaleValue = MAX_SCALE;
                }

                double f = (scaleValue / oldScale) - 1;

                double dx = (event.getSceneX() - (getBoundsInParent().getWidth() / 2
                    + getBoundsInParent().getMinX()));
                double dy = (event.getSceneY() - (getBoundsInParent().getHeight() / 2
                    + getBoundsInParent().getMinY()));

                scale.set(scaleValue);
                setTranslateX(getTranslateX() - f * dx);
                setTranslateY(getTranslateY() - f * dy);

                event.consume();
            });
        }
    }

    public class CustomScrollPane extends ScrollPane {

        private static final double MIN_SCALE = 0.1d;
        private static final double MAX_SCALE = 10.0d;

        private DoubleProperty scale = new SimpleDoubleProperty(1.0);

        private Group zoomGroup;

        private javafx.scene.Node content;

        private Scale scaleTransform;

        public CustomScrollPane(Pane layout) {
            super(layout);

            this.content = layout;

            Group contentGroup = new Group();
            zoomGroup = new Group();
            contentGroup.getChildren().add(zoomGroup);
            zoomGroup.getChildren().add(content);
            setContent(contentGroup);

            layout.setOnScroll(event -> {

                //				double scaleValue = getScaleX();
                double scaleValue =
                    scale.getValue();// currently we only use Y, same value is used for X
                double oldScale = scaleValue;

                scaleValue *= Math.pow(1.01, event.getDeltaY());

                if (scaleValue <= MIN_SCALE) {
                    scaleValue = MIN_SCALE;
                } else if (scaleValue >= MAX_SCALE) {
                    scaleValue = MAX_SCALE;
                }

                scale.setValue(scaleValue);

                double f = (scaleValue / oldScale) - 1;

                double dx = (event.getSceneX() - (getBoundsInParent().getWidth() / 2
                    + getBoundsInParent().getMinX()));
                double dy = (event.getSceneY() - (getBoundsInParent().getHeight() / 2
                    + getBoundsInParent().getMinY()));

                zoomGroup.getTransforms()
                    .add(new Scale(scaleValue, scaleValue, getTranslateX() - f * dx,
                        getTranslateY() - f * dy));

                event.consume();
            });
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
