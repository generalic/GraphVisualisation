package hr.fer.zemris.graph;

import hr.fer.zemris.graph.layout.ForceDirectedLayout;
import hr.fer.zemris.util.GraphLoader;
import java.io.IOException;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/** Constructs a scene with a pannable Map background. */
public class PannableViewZoom extends Application {
    private Image backgroundImage;

    @Override
    public void init() {
        backgroundImage =
            new Image("http://www.narniaweb.com/wp-content/uploads/2009/08/NarniaMap.jpg");
    }

    private static final int WIDTH = 1200;
    private static final int HEIGHT = 700;

    private Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Drag the mouse to pan the map");

        GraphLoader loader = new GraphLoader("grid15.json");
        Graph graph = loader.getGraph();

        ForceDirectedLayout fda = new ForceDirectedLayout(graph, WIDTH, HEIGHT);

        Timeline gameLoop = new Timeline(new KeyFrame(Duration.millis(30), e -> {
            fda.run(10);
        }));
        gameLoop.setCycleCount(Animation.INDEFINITE);
        gameLoop.play();

        // wrap the scene contents in a pannable scroll pane.
        ScrollPane scroll = createScrollPane(fda);

        StackPane stackPane = new StackPane(scroll);
        stackPane.setStyle("-fx-background-color: #383838");
        // show the scene.
        scene = new Scene(stackPane, 1200, 700);
        scene.getStylesheets().addAll(getClass().getResource("boris.css").toExternalForm());
        stage.setScene(scene);
        //		stage.initStyle(StageStyle.UNDECORATED);
        //		stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        //		stage.setFullScreen(true);
        stage.show();

        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(newValue);
        });

        // bind the preferred size of the scroll area to the size of the scene.
        scroll.prefViewportWidthProperty().bind(scene.widthProperty());
        scroll.prefViewportHeightProperty().bind(scene.heightProperty());

        //		scroll.setOnMouseClicked(e -> {
        ////			System.out.println(scroll.widthProperty().get());
        ////			System.out.println(scroll.heightProperty().get());
        ////			System.out.println();
        ////			System.out.println(scroll.getViewportBounds());
        ////			System.out.println(scroll.getPrefViewportWidth());
        ////			System.out.println(scroll.getPrefViewportHeight());
        ////			System.out.println();
        ////			System.out.println(scroll.getScaleX());
        ////			System.out.println(scroll.getScaleY());
        //
        //			System.out.println(fda.getWidth());
        //			System.out.println(fda.getHeight());
        //
        ////			System.out.println(fda.getPrefWidth());
        ////			System.out.println(fda.getPrefHeight());
        //		});

        //		 center the scroll contents.
        scroll.setHvalue(scroll.getHmin() + (scroll.getHmax() - scroll.getHmin()) / 2);
        scroll.setVvalue(scroll.getVmin() + (scroll.getVmax() - scroll.getVmin()) / 2);
    }

    /** @return a ScrollPane which scrolls the layout. */
    private ScrollPane createScrollPane(ForceDirectedLayout layout) {
        Group innerGroup = new Group();
        innerGroup.getChildren().addAll(layout.getGraph().getEdges());
        innerGroup.getChildren().addAll(layout.getGraph().getNodes());

        ScrollPane scroll = new ScrollPane(layout);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setPannable(true);

        scroll.addEventFilter(ScrollEvent.ANY, new ZoomHandler(innerGroup));

        //		String css = getClass().getResource("boris.css").toExternalForm();
        //		scroll.setStyle(css);

        //		scroll.addEventFilter(ScrollEvent.ANY, event -> {
        //			event.consume();
        //		});

        //		scroll.setFitToWidth(true);
        //		scroll.setFitToHeight(true);

        return scroll;
    }

    private static final double MAX_SCALE = 2.5d;
    private static final double MIN_SCALE = .5d;

    private class ZoomHandler implements EventHandler<ScrollEvent> {

        private Node nodeToZoom;

        private ZoomHandler(Node nodeToZoom) {
            this.nodeToZoom = nodeToZoom;
        }

        @Override
        public void handle(ScrollEvent scrollEvent) {
            if (scrollEvent.isControlDown()) {
                final double scale = calculateScale(scrollEvent);
                nodeToZoom.setScaleX(scale);
                nodeToZoom.setScaleY(scale);
                scrollEvent.consume();
            }
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

    public static void main(String[] args) {
        launch(args);
    }
}
