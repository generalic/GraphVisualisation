package hr.fer.zemris.graph.test;

import hr.fer.zemris.graph.layout.ForceDirectedLayout;
import hr.fer.zemris.graph.node.Node;
import hr.fer.zemris.util.GraphLoader;
import java.io.IOException;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ZoomAndPanExample extends Application {

    private ScrollPane scrollPane = new ScrollPane();

    private final DoubleProperty zoomProperty = new SimpleDoubleProperty(1.0d);
    private final DoubleProperty deltaY = new SimpleDoubleProperty(0.0d);

    private final Group group = new Group();

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
        AnchorPane.setTopAnchor(scrollPane, 0.0d);
        AnchorPane.setRightAnchor(scrollPane, 0.0d);
        AnchorPane.setBottomAnchor(scrollPane, 0.0d);
        AnchorPane.setLeftAnchor(scrollPane, 0.0d);

        AnchorPane root = new AnchorPane();

        GraphLoader loader = new GraphLoader("miserables.json");
        Graph graph = loader.getGraph();

        ForceDirectedLayout fda = new ForceDirectedLayout(graph, 1200, 700);

        group.getChildren().add(fda);
        // create canvas
        PanAndZoomPane panAndZoomPane = new PanAndZoomPane();
        zoomProperty.bind(panAndZoomPane.myScale);
        deltaY.bind(panAndZoomPane.deltaY);
        panAndZoomPane.getChildren().add(group);

        SceneGestures sceneGestures = new SceneGestures(panAndZoomPane);

        scrollPane.setContent(panAndZoomPane);
        panAndZoomPane.toBack();
        fda.toBack();

        scrollPane.addEventFilter(MouseEvent.MOUSE_CLICKED,
            sceneGestures.getOnMouseClickedEventHandler());
        scrollPane.addEventFilter(MouseEvent.MOUSE_PRESSED,
            sceneGestures.getOnMousePressedEventHandler());
        scrollPane.addEventFilter(MouseEvent.MOUSE_DRAGGED,
            sceneGestures.getOnMouseDraggedEventHandler());
        scrollPane.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());

        root.getChildren().add(scrollPane);
        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().addAll(getClass().getResource("boris.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

        try {
            Thread.sleep(2000);
            Timeline gameLoop = new Timeline(new KeyFrame(Duration.millis(30), e -> {
                fda.run(10);
            }));
            gameLoop.setCycleCount(Animation.INDEFINITE);
            gameLoop.play();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class PanAndZoomPane extends Pane {

        private static final double DEFAULT_DELTA = 1.3d;

        private DoubleProperty myScale = new SimpleDoubleProperty(1.0);
        private DoubleProperty deltaY = new SimpleDoubleProperty(0.0);
        private Timeline timeline;

        public PanAndZoomPane() {
            this.timeline = new Timeline(60);

            // add scale transform
            scaleXProperty().bind(myScale);
            scaleYProperty().bind(myScale);
        }

        public double getScale() {
            return myScale.get();
        }

        public void setScale(double scale) {
            myScale.set(scale);
        }

        public void setPivot(double x, double y, double scale) {
            // note: pivot value must be untransformed, i. e. without scaling
            // timeline that scales and moves the node
            timeline.getKeyFrames().clear();
            timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(200),
                    new KeyValue(translateXProperty(), getTranslateX() - x)),
                new KeyFrame(Duration.millis(200),
                    new KeyValue(translateYProperty(), getTranslateY() - y)),
                new KeyFrame(Duration.millis(200), new KeyValue(myScale, scale))
            );
            timeline.play();
        }

        public void fitWidth() {
            double scale = getParent().getLayoutBounds().getMaxX() / getLayoutBounds().getMaxX();
            double oldScale = getScale();

            //			double f = (scale / oldScale) - 1;
            double f = scale - oldScale;

            double dx = getTranslateX()
                - getBoundsInParent().getMinX()
                - getBoundsInParent().getWidth() / 2;
            double dy = getTranslateY()
                - getBoundsInParent().getMinY()
                - getBoundsInParent().getHeight() / 2;

            double newX = f * dx + getBoundsInParent().getMinX();
            double newY = f * dy + getBoundsInParent().getMinY();

            setPivot(newX, newY, scale);
        }

        public void resetZoom() {
            double scale = 1.0d;

            double x = getTranslateX();
            double y = getTranslateY();

            setPivot(x, y, scale);
        }

        public double getDeltaY() {
            return deltaY.get();
        }

        public void setDeltaY(double dY) {
            deltaY.set(dY);
        }
    }

    /**
     * Mouse drag context used for scene and nodes.
     */
    private class DragContext {

        private double mouseAnchorX;
        private double mouseAnchorY;

        private double translateAnchorX;
        private double translateAnchorY;
    }

    /**
     * Listeners for making the scene's canvas draggable and zoomable
     */
    public class SceneGestures {

        private DragContext sceneDragContext = new DragContext();

        PanAndZoomPane panAndZoomPane;

        public SceneGestures(PanAndZoomPane canvas) {
            this.panAndZoomPane = canvas;
        }

        public EventHandler<MouseEvent> getOnMouseClickedEventHandler() {
            return onMouseClickedEventHandler;
        }

        public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
            return onMousePressedEventHandler;
        }

        public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
            return onMouseDraggedEventHandler;
        }

        public EventHandler<ScrollEvent> getOnScrollEventHandler() {
            return onScrollEventHandler;
        }

        private EventHandler<MouseEvent> onMousePressedEventHandler =
            new EventHandler<MouseEvent>() {

                public void handle(MouseEvent event) {
                    sceneDragContext.mouseAnchorX = event.getX();
                    sceneDragContext.mouseAnchorY = event.getY();

                    sceneDragContext.translateAnchorX = panAndZoomPane.getTranslateX();
                    sceneDragContext.translateAnchorY = panAndZoomPane.getTranslateY();
                }
            };

        private EventHandler<MouseEvent> onMouseDraggedEventHandler =
            new EventHandler<MouseEvent>() {
                public void handle(MouseEvent event) {
                    if (event.getTarget() instanceof Node) {
                        return;
                    }

                    panAndZoomPane.setTranslateX(sceneDragContext.translateAnchorX + event.getX()
                        - sceneDragContext.mouseAnchorX);
                    panAndZoomPane.setTranslateY(sceneDragContext.translateAnchorY + event.getY()
                        - sceneDragContext.mouseAnchorY);

                    event.consume();
                }
            };

        /**
         * Mouse wheel handler: zoom to pivot point
         */
        private EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<ScrollEvent>() {

            @Override
            public void handle(ScrollEvent event) {
                double delta = PanAndZoomPane.DEFAULT_DELTA;

                double scale =
                    panAndZoomPane.getScale(); // currently we only use Y, same value is used for X
                double oldScale = scale;

                panAndZoomPane.setDeltaY(event.getDeltaY());
                if (panAndZoomPane.deltaY.get() < 0) {
                    scale /= delta;
                } else {
                    scale *= delta;
                }

                double f = (scale / oldScale) - 1;

                Bounds bounds = panAndZoomPane.getBoundsInParent();

                double dx = (event.getX() - (bounds.getWidth() / 2 + bounds.getMinX()));
                double dy = (event.getY() - (bounds.getHeight() / 2 + bounds.getMinY()));

                panAndZoomPane.setPivot(f * dx, f * dy, scale);
                event.consume();
            }
        };

        /**
         * Mouse click handler
         */
        private EventHandler<MouseEvent> onMouseClickedEventHandler =
            new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {
                    if (event.getButton().equals(MouseButton.PRIMARY)) {
                        if (event.getClickCount() == 2) {
                            panAndZoomPane.resetZoom();
                        }
                    }
                    if (event.getButton().equals(MouseButton.SECONDARY)) {
                        if (event.getClickCount() == 2) {
                            panAndZoomPane.fitWidth();
                        }
                    }
                }
            };
    }
}
