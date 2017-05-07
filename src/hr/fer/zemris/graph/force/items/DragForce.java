package hr.fer.zemris.graph.force.items;

import hr.fer.zemris.graph.force.AbstractForce;
import hr.fer.zemris.graph.force.constant.ForceConstant;
import hr.fer.zemris.graph.node.Node;

/**
 * Created by generalic on 7.5.2016..
 */
public class DragForce extends AbstractForce implements IItemForce {

    private static final double DEFAULT_DRAG_COEFF = 0.01d;
    private static final double DEFAULT_MIN_DRAG_COEFF = 0.0d;
    private static final double DEFAULT_MAX_DRAG_COEFF = 0.1d;

    private ForceConstant dragCoefficient;

    public DragForce(double dragCoefficient) {
        super("Drag Force");
        this.dragCoefficient = new ForceConstant(
            "Drag Coefficient",
            dragCoefficient,
            DEFAULT_MIN_DRAG_COEFF,
            DEFAULT_MAX_DRAG_COEFF
        );
        constants.add(this.dragCoefficient);
    }

    public DragForce() {
        this(DEFAULT_DRAG_COEFF);
    }

    @Override
    public void calculateForce(Node node) {
        node.setForce(node.force.subtract(node.velocity.multiply(dragCoefficient.getValue())));
    }
}
