package hr.fer.zemris.graph.force.items;

import hr.fer.zemris.graph.force.AbstractForce;
import hr.fer.zemris.graph.force.constant.ForceConstant;
import hr.fer.zemris.graph.layout.ForceDirectedLayout;
import hr.fer.zemris.graph.node.Node;
import java.util.Objects;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;

/**
 * Created by generalic on 8.5.2016..
 */
public class CenterGravitationalForce extends AbstractForce implements IItemForce {

    public static final double DEFAULT_FORCE_CONSTANT = 1E-4f;
    public static final double DEFAULT_MIN_FORCE_CONSTANT = 0;
    public static final double DEFAULT_MAX_FORCE_CONSTANT = 1E-1f;

    private ForceConstant gravitationalConstant;

    /**
     * Create a new GravitationForce.
     *
     * @param gravitationalConstant the gravitational constant to use
     * in radians.
     */
    public CenterGravitationalForce(double gravitationalConstant, ForceDirectedLayout root) {
        super("Gravitational Force");
        this.gravitationalConstant = new ForceConstant(
            "Gravitational Constant",
            gravitationalConstant,
            DEFAULT_MIN_FORCE_CONSTANT,
            DEFAULT_MAX_FORCE_CONSTANT
        );
        constants.add(this.gravitationalConstant);
        this.root = root;
    }

    /**
     * Create a new GravitationalForce with default gravitational
     * constant and direction.
     */
    public CenterGravitationalForce(ForceDirectedLayout root) {
        this(DEFAULT_FORCE_CONSTANT, root);
    }

    ForceDirectedLayout root;

    @Override
    public void calculateForce(Node node) {
        double coefficient = gravitationalConstant.getValue() * node.getMass();

        if (Objects.isNull(root.centerPane)) {
            return;
        }
        Bounds b = root.centerPane.getBoundsInParent();
        Point2D c = new Point2D((b.getMaxX() - b.getMinX()) / 2, (b.getMaxY() - b.getMinY()) / 2);

        Point2D r = c.subtract(node.getPosition());
        if (Math.abs(r.getX()) < 100 || Math.abs(r.getY()) < 100) {
            return;
        }
        r = r.normalize().multiply(coefficient).multiply(1 / r.magnitude());

        node.setForce(node.getForce().add(r));
    }
}
