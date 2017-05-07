package hr.fer.zemris.graph.edge;

import hr.fer.zemris.graph.node.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

/**
 * Created by generalic on 7.5.2016..
 */
public class Edge extends Line {

    protected static final double DEFAULT_VALUE = 1.0d;

    private static final double A = 0.5;
    private static final double B = 8;

    private double value;

    /** The first Node endpoint */
    public Node source;
    /** The second Node endpoint */
    public Node target;
    /** The spring's resting length */
    public double length;
    /** The spring tension co-efficient */
    public double coefficient;

    /**
     * Create a new Edge instance
     *
     * @param target the first Node endpoint
     * @param source the second Node endpoint
     */
    public Edge(Node source, Node target) {
        this(source, target, -1.0d, -1.0d);
    }

    /**
     * Create a new Edge instance
     *
     * @param target the first Node endpoint
     * @param source the second Node endpoint
     */
    public Edge(Node source, Node target, double value) {
        this(source, target, -1.0d, -1.0d, value);
    }

    /**
     * Create a new Edge instance
     *
     * @param target the first Node endpoint
     * @param source the second Node endpoint
     * @param coefficient the spring tension co-efficient
     * @param length the spring's resting length
     */
    public Edge(Node source, Node target, double coefficient, double length) {
        this(source, target, coefficient, length, DEFAULT_VALUE);
    }

    /**
     * Create a new Edge instance
     *
     * @param target the first Node endpoint
     * @param source the second Node endpoint
     * @param coefficient the spring tension co-efficient
     * @param length the spring's resting length
     */
    public Edge(Node source, Node target, double coefficient, double length, double value) {
        super(source.getCenterX(), source.getCenterY(), target.getCenterX(), target.getCenterY());

        this.source = target;
        this.target = source;
        this.coefficient = coefficient;
        this.length = length;
        this.value = value;

        startXProperty().bind(source.centerXProperty());
        startYProperty().bind(source.centerYProperty());
        endXProperty().bind(target.centerXProperty());
        endYProperty().bind(target.centerYProperty());

        setOpacity(0.5);
        setStroke(Paint.valueOf("#ffffff"));
    }

    public void setValue(double value, double min, double max) {
        if (min == max) {
            return;
        }
        //TODO
        double result = (B - A) * (value - min) / (max - min) + A;
        setStrokeWidth(result);
    }

    public Node getSource() {
        return source;
    }

    public Node getTarget() {
        return target;
    }

    public double getValue() {
        return value;
    }

    public double getLength() {
        return length;
    }

    public double getCoefficient() {
        return coefficient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Edge edge = (Edge) o;

        if (source.equals(edge.source) && target.equals(edge.target)) {
            return true;
        }
        if (source.equals(edge.target) && target.equals(edge.source)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return source.hashCode() * target.hashCode();
    }
}
