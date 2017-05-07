package hr.fer.zemris.graph.force;

import hr.fer.zemris.graph.edge.Edge;
import hr.fer.zemris.graph.force.intergrator.Integrator;
import hr.fer.zemris.graph.force.intergrator.RungeKuttaIntegrator;
import hr.fer.zemris.graph.force.items.IItemForce;
import hr.fer.zemris.graph.force.springs.ISpringForce;
import hr.fer.zemris.graph.node.Node;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;

/**
 * Created by generalic on 7.5.2016..
 */
public class ForceSimulator {

    private List<Node> nodes;
    private List<Edge> edges;

    private List<IItemForce> itemForces;
    private List<ISpringForce> springForces;

    private Integrator integrator;

    private DoubleProperty speedLimit = new SimpleDoubleProperty(5.0d);

    public ForceSimulator(Integrator integrator) {
        this.integrator = integrator;

        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();

        this.itemForces = new ArrayList<>();
        this.springForces = new ArrayList<>();
    }

    public ForceSimulator() {
        this(new RungeKuttaIntegrator());
    }

    public double getSpeedLimit() {
        return speedLimit.get();
    }

    public DoubleProperty speedLimitProperty() {
        return speedLimit;
    }

    public void setSpeedLimit(double speedLimit) {
        this.speedLimit.set(speedLimit);
    }

    public void clear() {
        nodes.clear();
        edges.clear();
    }

    public void addItemForce(IItemForce f) {
        itemForces.add(f);
    }

    public void addSpringForce(ISpringForce f) {
        springForces.add(f);
    }

    public void addItem(Node node) {
        nodes.add(node);
    }

    public void removeItem(Node node) {
        nodes.remove(node);
    }

    public void addSpring(Edge edge) {
        edges.add(edge);
    }

    public void addSpring(Node u, Node v) {
        edges.add(new Edge(u, v));
    }

    public void addSpring(Node u, Node v, double length, double coefficient) {
        edges.add(new Edge(u, v, length, coefficient));
    }

    public List<IItemForce> getItemForces() {
        return itemForces;
    }

    public List<IForce> getForces() {
        List<IForce> forces = new ArrayList<>(itemForces);
        forces.addAll(springForces);
        return forces;
    }

    public List<ISpringForce> getSpringForces() {
        return springForces;
    }

    public List<Node> getNodes() {
        return new ArrayList<>(nodes);
    }

    public List<Edge> getEdges() {
        return new ArrayList<>(edges);
    }

    public void run(double timeStep) {
        accumulate();
        integrator.integrate(this, timeStep);
    }

    public void accumulate() {
        itemForces.forEach(f -> f.init(this));
        springForces.forEach(f -> f.init(this));

        nodes.forEach(n -> {
            n.setForce(Point2D.ZERO);
            itemForces.forEach(f -> f.calculateForce(n));
        });

        edges.forEach(s -> springForces.forEach(f -> f.calculateForce(s)));
    }
}
