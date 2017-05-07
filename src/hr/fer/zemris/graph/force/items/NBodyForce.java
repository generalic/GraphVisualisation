package hr.fer.zemris.graph.force.items;

import hr.fer.zemris.graph.force.AbstractForce;
import hr.fer.zemris.graph.force.ForceSimulator;
import hr.fer.zemris.graph.force.constant.ForceConstant;
import hr.fer.zemris.graph.node.Node;
import hr.fer.zemris.util.RandomProvider;
import java.util.Objects;
import java.util.Random;
import javafx.geometry.Point2D;

/**
 * Created by generalic on 7.5.2016..
 */
public class NBodyForce extends AbstractForce implements IItemForce {

    private static final double DEFAULT_GRAV_CONSTANT = -1d;
    private static final double DEFAULT_MIN_GRAV_CONSTANT = -10d;
    private static final double DEFAULT_MAX_GRAV_CONSTANT = 10d;

    private static final double DEFAULT_DISTANCE = -1d;
    private static final double DEFAULT_MIN_DISTANCE = -1d;
    private static final double DEFAULT_MAX_DISTANCE = 500d;

    private static final double DEFAULT_THETA = 0.9d;
    private static final double DEFAULT_MIN_THETA = 0.0d;
    private static final double DEFAULT_MAX_THETA = 1.0d;

    private ForceConstant gravitationalConstant;
    private ForceConstant minDistance;
    private ForceConstant theta;

    private QuadTreeNode root;

    private double xMin;
    private double xMax;
    private double yMin;
    private double yMax;

    private Random rand = RandomProvider.get();

    /**
     * Create a new NBodyForce.
     *
     * @param gravitationalConstant the gravitational constant to use. Nodes will
     * attract each other if this value is positive, and will repel each
     * other if it is negative.
     * @param minDistance the distance within which two particles will
     * interact. If -1, the value is treated as infinite.
     * @param theta the Barnes-Hut parameter theta, which controls when
     * an aggregated mass is used rather than drilling down to individual
     * value mass values.
     */
    public NBodyForce(double gravitationalConstant, double minDistance, double theta) {
        super("NBody Force");
        this.gravitationalConstant = new ForceConstant(
            "Gravitational Constant",
            gravitationalConstant,
            DEFAULT_MIN_GRAV_CONSTANT,
            DEFAULT_MAX_GRAV_CONSTANT
        );
        this.minDistance = new ForceConstant(
            "Distance",
            minDistance,
            DEFAULT_MIN_DISTANCE,
            DEFAULT_MAX_DISTANCE
        );
        this.theta = new ForceConstant(
            "Barnes - Hut Theta",
            theta,
            DEFAULT_MIN_THETA,
            DEFAULT_MAX_THETA
        );
        constants.add(this.gravitationalConstant);
        constants.add(this.minDistance);
        constants.add(this.theta);
        root = new QuadTreeNode();
    }

    /**
     * Create a new NBodyForce with default parameters.
     */
    public NBodyForce() {
        this(DEFAULT_GRAV_CONSTANT, DEFAULT_DISTANCE, DEFAULT_THETA);
    }

    @Override
    public void init(ForceSimulator simulator) {
        clear();

        // compute and squarify bounds of quadtree
        double x1 = Double.MAX_VALUE;
        double y1 = Double.MAX_VALUE;
        double x2 = Double.MIN_VALUE;
        double y2 = Double.MIN_VALUE;

        for (Node i : simulator.getNodes()) {
            double x = i.getPosition().getX();
            double y = i.getPosition().getY();
            if (x < x1) {
                x1 = x;
            }
            if (y < y1) {
                y1 = y;
            }
            if (x > x2) {
                x2 = x;
            }
            if (y > y2) {
                y2 = y;
            }
        }
        double dx = x2 - x1;
        double dy = y2 - y1;
        if (dx > dy) {
            y2 = y1 + dx;
        } else {
            x2 = x1 + dy;
        }
        setBounds(x1, y1, x2, y2);

        // insert items into quadtree
        simulator.getNodes().forEach(this::insert);

        // calculate magnitudes and centers of mass
        calcMass(root);
    }

    @Override
    public void calculateForce(Node node) {
        try {
            forceHelper(node, root, xMin, yMin, xMax, yMax);
        } catch (StackOverflowError e) {
            // TODO: safe to remove?
            e.printStackTrace();
        }
    }

    /**
     * Inserts an value into the quadtree.
     *
     * @param node the ForceItem to add.
     * @throws IllegalStateException if the current location of the value is
     * outside the bounds of the quadtree
     */
    public void insert(Node node) {
        // insert value into the quadtrees
        try {
            insert(node, root, xMin, yMin, xMax, yMax);
        } catch (StackOverflowError e) {
            // TODO: safe to remove?
            e.printStackTrace();
        }
    }

    private void insert(Node p, QuadTreeNode n, double x1, double y1, double x2, double y2) {
        // try to insert particle p at node n in the quadtree
        // by construction, each leaf will contain either 1 or 0 particles
        if (n.hasChildren) {
            // n contains more than 1 particle
            insertHelper(p, n, x1, y1, x2, y2);
        } else if (Objects.nonNull(n.value)) {
            // n contains 1 particle
            if (isSameLocation(n.value, p)) {
                insertHelper(p, n, x1, y1, x2, y2);
            } else {
                Node v = n.value;
                n.value = null;
                insertHelper(v, n, x1, y1, x2, y2);
                insertHelper(p, n, x1, y1, x2, y2);
            }
        } else {
            // n is empty, so is a leaf
            n.value = p;
        }
    }

    private static final double NEARBY_THRESHOLD = 0.01;

    private static boolean isSameLocation(Node f1, Node f2) {
        Point2D diff = f1.getPosition().subtract(f2.getPosition());
        return Math.abs(diff.getX()) < NEARBY_THRESHOLD && Math.abs(diff.getY()) < NEARBY_THRESHOLD;
    }

    private void insertHelper(Node p, QuadTreeNode n, double x1, double y1, double x2, double y2) {
        Point2D point = p.getPosition();
        Point2D split = new Point2D(x1, y1).add(x2, y2).multiply(0.5);

        int index = (point.getX() >= split.getX() ? 1 : 0) + (point.getY() >= split.getY() ? 2 : 0);

        // create new child node, if necessary
        if (Objects.isNull(n.children[index])) {
            n.children[index] = new QuadTreeNode();
            n.hasChildren = true;
        }

        //update bounds
        if (index == 1 || index == 3) {
            x1 = split.getX();
        } else {
            x2 = split.getX();
        }
        if (index > 1) {
            y1 = split.getY();
        } else {
            y2 = split.getY();
        }

        // recursive
        insert(p, n.children[index], x1, y1, x2, y2);
    }

    private void calcMass(QuadTreeNode n) {
        Point2D centerOfMass = new Point2D(0, 0);
        n.mass = 0;
        if (n.hasChildren) {
            for (QuadTreeNode c : n.children) {
                if (Objects.nonNull(c)) {
                    calcMass(c);
                    n.mass += c.mass;
                    centerOfMass = centerOfMass.add(c.centerOfMass.multiply(c.mass));
                }
            }
        }
        if (Objects.nonNull(n.value)) {
            Node value = n.value;
            n.mass += value.getMass();
            centerOfMass = centerOfMass.add(value.getPosition().multiply(value.getMass()));
        }

        n.centerOfMass = centerOfMass.multiply(1 / n.mass);
    }

    private void clear() {
        root = new QuadTreeNode();
    }

    private void forceHelper(Node node, QuadTreeNode n, double x1, double y1, double x2,
        double y2) {
        Point2D diff = n.centerOfMass.subtract(node.getPosition());
        double r = diff.magnitude();
        boolean same = false;

        if (r == 0.0d) {
            // if items are in the exact same place, add some noise
            diff =
                new Point2D((rand.nextDouble() - 0.5d) / 50.0d, (rand.nextDouble() - 0.5d) / 50.0d);
            r = diff.magnitude();
            same = true;
        }

        boolean minDist = minDistance.getValue() > 0.0d && r > minDistance.getValue();

        // the Barnes-Hut approximation criteria is if the ratio of the
        // size of the quadtree box to the distance between the point and
        // the box's center of mass is beneath some threshold theta.
        if ((!n.hasChildren && n.value != node) || !same && (x2 - x1) / r < theta.getValue()) {
            if (minDist) {
                return;
            }
            // either only 1 particle or we meet criteria
            // for Barnes-Hut approximation, so calc force_constant
            double v = gravitationalConstant.getValue() * node.mass * n.mass / (r * r * r);
            node.setForce(node.getForce().add(diff.multiply(v)));
        } else if (n.hasChildren) {
            // recurse for more accurate calculation
            Point2D split = new Point2D(x1, y1).add(x2, y2).multiply(0.5);
            for (int i = 0; i < n.children.length; i++) {
                if (Objects.nonNull(n.children[i])) {
                    forceHelper(
                        node,
                        n.children[i],
                        i == 1 || i == 3 ? split.getX() : x1,
                        i > 1 ? split.getY() : y1,
                        i == 1 || i == 3 ? x2 : split.getX(),
                        i > 1 ? y2 : split.getY()
                    );
                }
            }
            if (minDist) {
                return;
            }
            if (Objects.nonNull(n.value) && n.value != node) {
                double v =
                    gravitationalConstant.getValue() * node.mass * n.value.mass / (r * r * r);
                node.setForce(node.force.add(diff.multiply(v)));
            }
        }
    }

    /**
     * Set the bounds of the region for which to compute the n-body simulation
     *
     * @param xMin the minimum x-coordinate
     * @param yMin the minimum y-coordinate
     * @param xMax the maximum x-coordinate
     * @param yMax the maximum y-coordinate
     */
    private void setBounds(double xMin, double yMin, double xMax, double yMax) {
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
    }

    private static class QuadTreeNode {

        /** Total mass by this node. */
        private double mass;
        /** Center of mass for this node. */
        private Point2D centerOfMass;
        /** Node in this node, null if node has children. */
        private Node value;
        /** Children nodes. */
        private QuadTreeNode[] children;
        /** {@code true} if this node has children, {@code false} otherwise */
        private boolean hasChildren;

        public QuadTreeNode() {
            this.centerOfMass = Point2D.ZERO;
            this.children = new QuadTreeNode[4];
            hasChildren = false;
        }
    }
}


