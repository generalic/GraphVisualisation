package hr.fer.zemris.graph.node;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.Tooltip;
import javafx.scene.shape.Circle;

/**
 * Created by generalic on 7.5.2016..
 */
public class Node extends Circle {

    private static final double RADIUS = 5;
    protected static final int DEFAULT_GROUP = 0;

    private final String name;
    private final int group;

    private final BooleanProperty fixed = new SimpleBooleanProperty(false);

    private final Tooltip nameLabel;

    /** The mass value of this Node. */
    public double mass;
    /** The values of the forces acting on this Node. */
    public Point2D force;
    /** The velocity values of this Node. */
    public Point2D velocity;
    /** The location values of this Node. */
    /** The previous location values of this Node. */
    public Point2D previousPosition;

    /** Temporary variables for Runge-Kutta integration */
    public Point2D[] k;
    /** Temporary variables for Runge-Kutta integration */
    public Point2D[] l;

    public Node(String name) {
        this(name, DEFAULT_GROUP);
    }

    public Node(String name, int group) {
        super(0, 0, RADIUS);
        this.name = name;
        this.group = group;

        this.nameLabel = new Tooltip(name);

        setOnMouseEntered(e -> {
            fixed.setValue(true);
            setCursor(Cursor.HAND);
            e.consume();
        });

        setOnMouseExited(e -> {
            fixed.setValue(false);
            e.consume();
        });

        setOnMousePressed(e -> {
            fixed.setValue(true);
            nameLabel.show(this, e.getSceneX(), e.getSceneY());
            e.consume();
        });

        setOnMouseReleased(e -> {
            fixed.setValue(false);
            nameLabel.hide();
            e.consume();
        });

        setOnMouseDragged(e -> {
            nameLabel.hide();
            fixed.setValue(true);
            setCenterX(e.getX());
            setCenterY(e.getY());
            e.consume();
        });

        mass = 1.0d;
        setForce(Point2D.ZERO);
        setVelocity(Point2D.ZERO);
        setPosition(Point2D.ZERO);
        setPreviousPosition(Point2D.ZERO);
        k = new Point2D[4];
        l = new Point2D[4];
    }

    public String getName() {
        return name;
    }

    public int getGroup() {
        return group;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public Point2D getPosition() {
        return new Point2D(getCenterX(), getCenterY());
    }

    public void setPosition(Point2D position) {
        if (fixed.get()) {
            return;
        }
        setCenterX(position.getX());
        setCenterY(position.getY());
    }

    public Point2D getPreviousPosition() {
        return previousPosition;
    }

    public void setPreviousPosition(Point2D previousPosition) {
        this.previousPosition = new Point2D(previousPosition.getX(), previousPosition.getY());
    }

    public Point2D getForce() {
        return force;
    }

    public void setForce(Point2D force) {
        this.force = new Point2D(force.getX(), force.getY());
    }

    public Point2D getVelocity() {
        return velocity;
    }

    public void setVelocity(Point2D velocity) {
        this.velocity = new Point2D(velocity.getX(), velocity.getY());
    }

    public boolean getFixed() {
        return fixed.get();
    }

    public BooleanProperty fixedProperty() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed.set(fixed);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Node other = (Node) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }
}
