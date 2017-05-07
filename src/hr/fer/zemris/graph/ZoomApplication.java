package hr.fer.zemris.graph;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * Created by generalic on 12.5.2016..
 */
public class ZoomApplication extends Application {

    public static class PannableCanvas extends Pane {

        DoubleProperty myScale = new SimpleDoubleProperty(1.0);

        public PannableCanvas() {

            setPrefSize(600, 600);
            setStyle("-fx-background-color: lightgrey; -fx-border-color: blue;");

            // add scale transform
            scaleXProperty().bind(myScale);
            scaleYProperty().bind(myScale);

            // logging
            addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                System.out.println(
                    "canvas event: " + (((event.getSceneX() - getBoundsInParent().getMinX())
                        / getScale()) + ", scale: " + getScale())
                );
                System.out.println("canvas bounds: " + getBoundsInParent());
            });
        }

        /**
         * Add a grid to the canvas, send it to back
         */
        public void addGrid() {

            double w = getBoundsInLocal().getWidth();
            double h = getBoundsInLocal().getHeight();

            // add grid
            Canvas grid = new Canvas(w, h);

            // don't catch mouse events
            grid.setMouseTransparent(true);

            GraphicsContext gc = grid.getGraphicsContext2D();

            gc.setStroke(Color.GRAY);
            gc.setLineWidth(1);

            // draw grid lines
            double offset = 50;
            for (double i = offset; i < w; i += offset) {
                // vertical
                gc.strokeLine(i, 0, i, h);
                // horizontal
                gc.strokeLine(0, i, w, i);
            }

            getChildren().add(grid);

            grid.toBack();
        }

        public double getScale() {
            return myScale.get();
        }

        /**
         * Set x/y scale
         */
        public void setScale(double scale) {
            myScale.set(scale);
        }

        /**
         * Set x/y pivot points
         */
        public void setPivot(double x, double y) {
            setTranslateX(getTranslateX() - x);
            setTranslateY(getTranslateY() - y);
        }
    }

    /**
     * Mouse drag context used for scene and nodes.
     */
    class DragContext {

        double mouseAnchorX;
        double mouseAnchorY;

        double translateAnchorX;
        double translateAnchorY;
    }

    /**
     * Listeners for making the nodes draggable via left mouse button. Considers if parent is
     * zoomed.
     */
    class NodeGestures {

        private DragContext nodeDragContext = new DragContext();

        PannableCanvas canvas;

        public NodeGestures(PannableCanvas canvas) {
            this.canvas = canvas;
        }

        public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
            return onMousePressedEventHandler;
        }

        public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
            return onMouseDraggedEventHandler;
        }

        private EventHandler<MouseEvent> onMousePressedEventHandler =
            new EventHandler<MouseEvent>() {

                public void handle(MouseEvent event) {

                    // left mouse button => dragging
                    if (!event.isPrimaryButtonDown()) {
                        return;
                    }

                    nodeDragContext.mouseAnchorX = event.getSceneX();
                    nodeDragContext.mouseAnchorY = event.getSceneY();

                    Node node = (Node) event.getSource();

                    nodeDragContext.translateAnchorX = node.getTranslateX();
                    nodeDragContext.translateAnchorY = node.getTranslateY();
                }
            };

        private EventHandler<MouseEvent> onMouseDraggedEventHandler =
            new EventHandler<MouseEvent>() {
                public void handle(MouseEvent event) {

                    // left mouse button => dragging
                    if (!event.isPrimaryButtonDown()) {
                        return;
                    }

                    double scale = canvas.getScale();

                    Node node = (Node) event.getSource();

                    node.setTranslateX(nodeDragContext.translateAnchorX + ((event.getSceneX()
                        - nodeDragContext.mouseAnchorX) / scale));
                    node.setTranslateY(nodeDragContext.translateAnchorY + ((event.getSceneY()
                        - nodeDragContext.mouseAnchorY) / scale));

                    event.consume();
                }
            };
    }

    /**
     * Listeners for making the scene's canvas draggable and zoomable
     */
    class SceneGestures {

        private static final double MAX_SCALE = 10.0d;
        private static final double MIN_SCALE = .1d;

        private DragContext sceneDragContext = new DragContext();

        PannableCanvas canvas;

        public SceneGestures(PannableCanvas canvas) {
            this.canvas = canvas;
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

                    // right mouse button => panning
                    if (!event.isSecondaryButtonDown()) {
                        return;
                    }

                    sceneDragContext.mouseAnchorX = event.getSceneX();
                    sceneDragContext.mouseAnchorY = event.getSceneY();

                    sceneDragContext.translateAnchorX = canvas.getTranslateX();
                    sceneDragContext.translateAnchorY = canvas.getTranslateY();
                }
            };

        private EventHandler<MouseEvent> onMouseDraggedEventHandler =
            new EventHandler<MouseEvent>() {
                public void handle(MouseEvent event) {

                    // right mouse button => panning
                    if (!event.isSecondaryButtonDown()) {
                        return;
                    }

                    canvas.setTranslateX(sceneDragContext.translateAnchorX + event.getSceneX()
                        - sceneDragContext.mouseAnchorX);
                    canvas.setTranslateY(sceneDragContext.translateAnchorY + event.getSceneY()
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

                double scale =
                    canvas.getScale(); // currently we only use Y, same value is used for X
                double oldScale = scale;

                scale *= Math.pow(1.01, event.getDeltaY());

                if (scale <= MIN_SCALE) {
                    scale = MIN_SCALE;
                } else if (scale >= MAX_SCALE) {
                    scale = MAX_SCALE;
                }

                double f = (scale / oldScale) - 1;

                double dx = (event.getSceneX() - (canvas.getBoundsInParent().getWidth() / 2
                    + canvas.getBoundsInParent().getMinX()));
                double dy = (event.getSceneY() - (canvas.getBoundsInParent().getHeight() / 2
                    + canvas.getBoundsInParent().getMinY()));

                canvas.setScale(scale);
                canvas.setPivot(f * dx, f * dy);

                event.consume();
            }
        };
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        Group group = new Group();

        // create canvas
        PannableCanvas canvas = new PannableCanvas();

        // we don't want the canvas on the top/left in this example => just
        // translate it a bit
        canvas.setTranslateX(100);
        canvas.setTranslateY(100);

        // create sample nodes which can be dragged
        NodeGestures nodeGestures = new NodeGestures(canvas);

        Label label1 = new Label("Draggable node 1");
        label1.setTranslateX(10);
        label1.setTranslateY(10);
        label1.addEventFilter(MouseEvent.MOUSE_PRESSED,
            nodeGestures.getOnMousePressedEventHandler());
        label1.addEventFilter(MouseEvent.MOUSE_DRAGGED,
            nodeGestures.getOnMouseDraggedEventHandler());

        Label label2 = new Label("Draggable node 2");
        label2.setTranslateX(100);
        label2.setTranslateY(100);
        label2.addEventFilter(MouseEvent.MOUSE_PRESSED,
            nodeGestures.getOnMousePressedEventHandler());
        label2.addEventFilter(MouseEvent.MOUSE_DRAGGED,
            nodeGestures.getOnMouseDraggedEventHandler());

        Label label3 = new Label("Draggable node 3");
        label3.setTranslateX(200);
        label3.setTranslateY(200);
        label3.addEventFilter(MouseEvent.MOUSE_PRESSED,
            nodeGestures.getOnMousePressedEventHandler());
        label3.addEventFilter(MouseEvent.MOUSE_DRAGGED,
            nodeGestures.getOnMouseDraggedEventHandler());

        Circle circle1 = new Circle(300, 300, 50);
        circle1.setStroke(Color.ORANGE);
        circle1.setFill(Color.ORANGE.deriveColor(1, 1, 1, 0.5));
        circle1.addEventFilter(MouseEvent.MOUSE_PRESSED,
            nodeGestures.getOnMousePressedEventHandler());
        circle1.addEventFilter(MouseEvent.MOUSE_DRAGGED,
            nodeGestures.getOnMouseDraggedEventHandler());

        Rectangle rect1 = new Rectangle(100, 100);
        rect1.setTranslateX(450);
        rect1.setTranslateY(450);
        rect1.setStroke(Color.BLUE);
        rect1.setFill(Color.BLUE.deriveColor(1, 1, 1, 0.5));
        rect1.addEventFilter(MouseEvent.MOUSE_PRESSED,
            nodeGestures.getOnMousePressedEventHandler());
        rect1.addEventFilter(MouseEvent.MOUSE_DRAGGED,
            nodeGestures.getOnMouseDraggedEventHandler());

        canvas.getChildren().addAll(label1, label2, label3, circle1, rect1);

        group.getChildren().add(canvas);

        // create scene which can be dragged and zoomed
        Scene scene = new Scene(group, 1024, 768);

        SceneGestures sceneGestures = new SceneGestures(canvas);
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED,
            sceneGestures.getOnMousePressedEventHandler());
        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED,
            sceneGestures.getOnMouseDraggedEventHandler());
        scene.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());

        stage.setScene(scene);
        stage.show();

        canvas.addGrid();
    }
}
