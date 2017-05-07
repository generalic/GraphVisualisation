package hr.fer.zemris.graph.force.springs;

import hr.fer.zemris.graph.edge.Edge;
import hr.fer.zemris.graph.force.AbstractForce;
import hr.fer.zemris.graph.force.constant.ForceConstant;
import hr.fer.zemris.graph.node.Node;
import hr.fer.zemris.util.RandomProvider;
import java.util.Random;
import javafx.geometry.Point2D;

/**
 * Force function that computes the force_constant acting on ForceItems due to a
 * given Spring.
 *
 * Created by generalic on 7.5.2016..
 */
public class SpringForce extends AbstractForce implements ISpringForce {

    public static final double DEFAULT_SPRING_COEFF = 1E-4f;
    public static final double DEFAULT_MAX_SPRING_COEFF = 1E-3f;
    public static final double DEFAULT_MIN_SPRING_COEFF = 1E-5f;

    public static final double DEFAULT_SPRING_LENGTH = 50;
    public static final double DEFAULT_MIN_SPRING_LENGTH = 0;
    public static final double DEFAULT_MAX_SPRING_LENGTH = 200;

    private ForceConstant springCoefficient;
    private ForceConstant springLength;

    private Random rand = RandomProvider.get();

    /**
     * Create a new SpringForce.
     *
     * @param springCoefficient the default spring co-efficient to use. This will
     * be used if the spring's own co-efficient is less than zero.
     * @param defaultLength the default spring length to use. This will
     * be used if the spring's own length is less than zero.
     */
    public SpringForce(double springCoefficient, double defaultLength) {
        super("Spring Force");
        this.springCoefficient = new ForceConstant(
            "Spring Coefficient",
            springCoefficient,
            DEFAULT_MIN_SPRING_COEFF,
            DEFAULT_MAX_SPRING_COEFF
        );
        this.springLength = new ForceConstant(
            "Default Length",
            defaultLength,
            DEFAULT_MIN_SPRING_LENGTH,
            DEFAULT_MAX_SPRING_LENGTH
        );
        constants.add(this.springCoefficient);
        constants.add(this.springLength);
    }

    /**
     * Constructs a new SpringForce instance with default parameters.
     */
    public SpringForce() {
        this(DEFAULT_SPRING_COEFF, DEFAULT_SPRING_LENGTH);
    }

    /**
     * Calculates the force_constant vector acting on the items due to the given edge.
     *
     * @param edge the Edge for which to compute the force_constant
     */
    @Override
    public void calculateForce(Edge edge) {
        Node node1 = edge.source;
        Node node2 = edge.target;

        double length = (edge.length < 0 ? springLength.getValue() : edge.length);
        Point2D diff = node2.getPosition().subtract(node1.getPosition());
        double r = diff.magnitude();

        if (r == 0.0d) {
            diff = new Point2D(
                (rand.nextDouble() - 0.5d) / 50.0d,
                (rand.nextDouble() - 0.5d) / 50.0d
            );
            r = diff.magnitude();
        }

        double d = r - length;
        double coefficient =
            (edge.coefficient < 0 ? springCoefficient.getValue() : edge.coefficient) * d / r;

        node1.setForce(node1.force.add(diff.multiply(coefficient)));
        node2.setForce(node2.force.add(diff.multiply(-coefficient)));
    }
}
