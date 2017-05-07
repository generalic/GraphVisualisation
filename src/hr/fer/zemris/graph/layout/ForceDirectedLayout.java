package hr.fer.zemris.graph.layout;

import hr.fer.zemris.graph.Graph;
import hr.fer.zemris.graph.force.ForceSimulator;
import hr.fer.zemris.graph.force.items.DragForce;
import hr.fer.zemris.graph.force.items.GravitationalForce;
import hr.fer.zemris.graph.force.items.NBodyForce;
import hr.fer.zemris.graph.force.springs.SpringForce;
import hr.fer.zemris.graph.node.Node;
import hr.fer.zemris.util.RandomProvider;
import java.util.Random;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;

/**
 * Created by generalic on 8.5.2016..
 */
public class ForceDirectedLayout extends Pane {

    private Graph graph;

    private ForceSimulator simulator;

    private Random rand = RandomProvider.get();

    private static BooleanProperty initConfig = new SimpleBooleanProperty(true);

    private static Dimension2D dimension;

    static {
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        dimension = new Dimension2D(bounds.getWidth(), bounds.getHeight());
    }

    public ForceDirectedLayout(Graph graph) {
        this(graph, dimension.getWidth(), dimension.getHeight());
    }

    public BorderPane centerPane;

    public ForceDirectedLayout(Graph graph, double width, double height) {
        setWidth(width);
        setHeight(height);

        this.graph = graph;

        getChildren().addAll(graph.getEdges());
        getChildren().addAll(graph.getNodes());

        simulator = new ForceSimulator();
        simulator.addItemForce(new NBodyForce());
        simulator.addItemForce(new GravitationalForce());
        //		simulator.addItemForce(new CenterGravitationalForce(this));
        simulator.addItemForce(new DragForce());
        simulator.addSpringForce(new SpringForce());

        this.setStyle("-fx-background-color: #383838");
        //		this.setStyle("-fx-background-color: #ffffff");
        //		graph.getNodes().forEach(n -> n.setFill(Paint.valueOf("#ffffff")));
        //		graph.getNodes().forEach(n -> n.setStrokeWidth(2));
        //		graph.getNodes().forEach(n -> n.setStroke(Paint.valueOf("#000000")));
        //		graph.getEdges().forEach(e -> e.setStroke(Paint.valueOf("#000000")));

        initSimulator();
        init();
    }

    private void init() {
        if (initConfig.get()) {
            randomConfiguration();
        } else {
            simulatedAnnealingConfiguration();
        }
    }

    public BooleanProperty initConfigProperty() {
        return initConfig;
    }

    private void simulatedAnnealingConfiguration() {
        PrePositionStrategy strategy = new PrePositionStrategy(graph, this);
        strategy.run();
    }

    private void randomConfiguration() {
        double scaleW = 0.1 * getWidth() / 2;
        double scaleH = 0.1 * getHeight() / 2;

        for (Node node : graph.getNodes()) {
            Point2D p = new Point2D(
                getLayoutX() + getWidth() / 2 + rand.nextDouble() * scaleW,
                getLayoutY() + getHeight() / 2 + rand.nextDouble() * scaleH
            );
            node.setPosition(p);
        }
    }

    public void run(double timeStep) {
        simulator.run(timeStep);
        updateNodePositions();
    }

    private void initSimulator() {
        graph.getNodes().forEach(simulator::addItem);
        graph.getEdges().forEach(simulator::addSpring);
    }

    private void updateNodePositions() {
        Bounds bounds = getBoundsInParent();
        double x1 = 0, x2 = 0, y1 = 0, y2 = 0;

        if (bounds != null) {
            x1 = bounds.getMinX();
            y1 = bounds.getMinY();
            x2 = bounds.getMaxX();
            y2 = bounds.getMaxY();
        }

        // update positions

        for (Node item : graph.getNodes()) {
            if (item.getFixed()) {
                item.setForce(new Point2D(0, 0));
                item.setVelocity(new Point2D(0, 0));
                continue;
            }

            double x = item.getPosition().getX();
            double y = item.getPosition().getY();

            if (bounds != null) {
                Bounds b = item.getBoundsInParent();
                double hw = b.getWidth() / 2;
                double hh = b.getHeight() / 2;
                if (x + hw > x2) {
                    x = x2 - hw;
                }
                if (x - hw < x1) {
                    x = x1 + hw;
                }
                if (y + hh > y2) {
                    y = y2 - hh;
                }
                if (y - hh < y1) {
                    y = y1 + hh;
                }
            }

            item.setPosition(new Point2D(x, y));
        }
    }

    public Graph getGraph() {
        return graph;
    }

    public ForceSimulator getSimulator() {
        return simulator;
    }
}
