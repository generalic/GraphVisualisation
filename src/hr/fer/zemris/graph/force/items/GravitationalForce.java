package hr.fer.zemris.graph.force.items;

import hr.fer.zemris.graph.force.AbstractForce;
import hr.fer.zemris.graph.force.constant.ForceConstant;
import hr.fer.zemris.graph.node.Node;
import javafx.geometry.Point2D;

/**
 * Created by generalic on 8.5.2016..
 */
public class GravitationalForce extends AbstractForce implements IItemForce {

    public static final double DEFAULT_FORCE_CONSTANT = 1E-4f;
    public static final double DEFAULT_MIN_FORCE_CONSTANT = 0;
    public static final double DEFAULT_MAX_FORCE_CONSTANT = 1E-3f;

    public static final double DEFAULT_DIRECTION = -Math.PI / 2;
    public static final double DEFAULT_MIN_DIRECTION = -Math.PI;
    public static final double DEFAULT_MAX_DIRECTION = Math.PI;

    private ForceConstant gravitationalConstant;
    private ForceConstant direction;

    /**
     * Create a new GravitationForce.
     *
     * @param gravitationalConstant the gravitational constant to use
     * @param direction the direction in which gravity should act,
     * in radians.
     */
    public GravitationalForce(double gravitationalConstant, double direction) {
        super("Gravitational Force");
        this.gravitationalConstant = new ForceConstant(
            "Gravitational Constant",
            gravitationalConstant,
            DEFAULT_MIN_FORCE_CONSTANT,
            DEFAULT_MAX_FORCE_CONSTANT
        );
        this.direction = new ForceConstant(
            "Direction",
            direction,
            DEFAULT_MIN_DIRECTION,
            DEFAULT_MAX_DIRECTION
        );
        constants.add(this.gravitationalConstant);
        constants.add(this.direction);
    }

    /**
     * Create a new GravitationalForce with default gravitational
     * constant and direction.
     */
    public GravitationalForce() {
        this(DEFAULT_FORCE_CONSTANT, DEFAULT_DIRECTION);
    }

    @Override
    public void calculateForce(Node node) {
        double coefficient = gravitationalConstant.getValue() * node.getMass();

        Point2D result = new Point2D(
            Math.cos(direction.getValue()) * coefficient,
            Math.sin(direction.getValue()) * coefficient
        );

        node.setForce(node.getForce().add(result));
    }
}
