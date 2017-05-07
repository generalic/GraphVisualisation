package hr.fer.zemris.graph.layout;

import hr.fer.zemris.graph.Graph;
import hr.fer.zemris.graph.edge.Edge;
import hr.fer.zemris.graph.node.Node;
import java.util.Random;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;

public class PrePositionStrategy {

    private static final double GAMA = 0.1;

    private Pane root;

    private Graph graph;

    private double k;

    private double t = 1000;

    private double maxIterations = 700;

    private double width;
    private double height;

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public PrePositionStrategy(Graph graph, Pane root) {
        setWidth(root.getWidth());
        setHeight(root.getHeight());

        this.root = root;
        this.graph = graph;

        init();
    }

    private static final double ALPHA = 0.1;

    private void init() {
        double area = getWidth() * getHeight();
        int numberOfNodes = graph.getNodes().size();
        this.k = GAMA * Math.sqrt(area / numberOfNodes);

        this.t = getWidth() / 10;

        Random rand = new Random(42);
        double scaleW = ALPHA * getWidth() / 2;
        double scaleH = ALPHA * getHeight() / 2;

        for (Node node : graph.getNodes()) {
            Point2D p = new Point2D(
                root.getLayoutX() + getWidth() / 2 + rand.nextDouble() * scaleW,
                root.getLayoutY() + getHeight() / 2 + rand.nextDouble() * scaleH
            );
            node.setPosition(p);
        }
    }

    private double i = 0;

    public void run() {
        for (int i = 0; i < maxIterations; i++) {
            {
                for (Node v : graph.getNodes()) {
                    if (v.getFixed()) {
                        continue;
                    }
                    calcRepulsion(v);
                }

                for (Edge e : graph.getEdges()) {
                    calcAttraction(e);
                }

                for (Node v : graph.getNodes()) {
                    if (v.getFixed()) {
                        continue;
                    }
                    calcPosition(v);
                }
                coolTemperature(i);
            }
        }
    }

    private void calcRepulsion(Node v) {
        v.setForce(new Point2D(0, 0));
        for (Node u : graph.getNodes()) {
            if (!u.equals(v)) {
                Point2D vPos = v.getPosition();
                Point2D uPos = u.getPosition();
                Point2D delta = vPos.subtract(uPos);
                Point2D c = delta.normalize().multiply(calculateRepulsiveForce(delta));
                v.setForce(v.getForce().add(c));
            }
        }
    }

    private void calcAttraction(Edge e) {
        Node s = e.getSource();
        Node t = e.getTarget();

        Point2D delta = s.getPosition().subtract(t.getPosition());
        Point2D c = delta.normalize().multiply(calculateAttractiveForce(delta));

        s.setForce(s.getForce().subtract(c));
        t.setForce(t.getForce().add(c));
    }

    private void calcPosition(Node v) {
        Point2D vForce = v.getForce();
        Point2D c = vForce.normalize().multiply(Math.min(vForce.magnitude(), t));
        v.setPosition(v.getPosition().add(c));

        Bounds b = root.getBoundsInLocal();
        double borderWidth = getWidth() / 50.0;

        double x = v.getCenterX();
        if (x < b.getMinX() + borderWidth) {
            x = b.getMinX() + borderWidth + Math.random() * borderWidth * 2.0;
        } else if (x > (b.getMaxX() - borderWidth)) {
            x = b.getMaxX() - borderWidth - Math.random() * borderWidth * 2.0;
        }

        double y = v.getCenterY();
        if (y < b.getMinY() + borderWidth) {
            y = b.getMinY() + borderWidth + Math.random() * borderWidth * 2.0;
        } else if (y > (b.getMaxY() - borderWidth)) {
            y = b.getMaxY() - borderWidth - Math.random() * borderWidth * 2.0;
        }

        v.setPosition(new Point2D(x, y));
    }

    private static final double BETA = 2;

    private void coolTemperature(double k) {
        t *= (1.0 - k / maxIterations);
        //		t = getWidth() / (100 * Math.log(k));
        //		t = getWidth() / 10 - k * BETA;
        //		t = t / (1 + BETA * t);
    }

    private double calculateAttractiveForce(Point2D vector) {
        double x = vector.magnitude();
        return x * x / k;
    }

    private double calculateRepulsiveForce(Point2D vector) {
        double x = vector.magnitude();
        if (Double.compare(x, 0.0) == 0) {
            return k * k;
        }
        return k * k / x;
    }
}
