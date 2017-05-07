package hr.fer.zemris.graph.layout;

import hr.fer.zemris.graph.Graph;
import hr.fer.zemris.graph.edge.Edge;
import hr.fer.zemris.graph.node.Node;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;

public class FruchtermanReingoldLayout extends Pane {

    private static final double GAMA = 0.1;

    private Graph graph;

    private double k;

    private double t = 1000;

    private double maxIterations = 700;

    public FruchtermanReingoldLayout(Graph graph, double width, double height) {
        setWidth(width);
        setHeight(height);

        this.graph = graph;

        getChildren().addAll(graph.getEdges());
        getChildren().addAll(graph.getNodes());

        //		this.setStyle("-fx-background-color: #383838");
        this.setStyle("-fx-background-color: #ffffff");
        graph.getNodes().forEach(n -> n.setFill(Paint.valueOf("#029DFA")));
        graph.getEdges().forEach(e -> e.setStroke(Paint.valueOf("#000000")));

        init();
        //		init2();
    }

    Map<Node, Integer> map = new HashMap<>();

    private void updateNodeOccurency(Node n) {
        Integer i = map.get(n);
        map.put(n, Objects.isNull(i) ? 0 : i + 1);
    }

    private void init2() {

        graph.getEdges().forEach(e -> {
            updateNodeOccurency(e.getSource());
            updateNodeOccurency(e.getTarget());
        });

        Node vk = map.entrySet().stream()
            .sorted((e1, e2) -> -Integer.compare(e1.getValue(), e2.getValue()))
            .map(HashMap.Entry::getKey)
            .findFirst()
            .get();

        Bounds b = getBoundsInLocal();
        Point2D p = new Point2D(getLayoutX() + getWidth() / 2, getLayoutY() + getHeight() / 2);
        vk.setPosition(p);

        preposition(vk);

        graph.getNodes().forEach(n -> n.setFixed(false));
        System.out.println("gotov");
    }

    private double mul = 1;

    private void preposition(Node vk) {
        if (vk.getFixed()) {
            return;
        }
        List<Edge> vkDirectEdges = graph.getEdges().stream()
            .filter(e -> e.getSource().equals(vk) || e.getTarget().equals(vk))
            //				.map(e -> e.getSource().equals(vk) ? e.getTarget() : e.getSource())
            .collect(Collectors.toList());

        long vkDirectCount = vkDirectEdges.size();

        double degree = 2 * Math.PI / vkDirectCount;

        double startDegree = 0;

        List<Node> vkDirect = vkDirectEdges.stream()
            .map(e -> e.getSource().equals(vk) ? e.getTarget() : e.getSource())
            .collect(Collectors.toList());

        for (Edge e : vkDirectEdges) {
            Node n = e.getSource().equals(vk) ? e.getTarget() : e.getSource();
            Point2D np = vk.getPosition()
                .add(new Point2D(Math.cos(startDegree), Math.sin(startDegree)).multiply(mul * 0.2));
            n.setPosition(np);
            startDegree += degree;
        }

        mul *= 2;

        vk.setFixed(true);

        vkDirect.forEach(this::preposition);
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
                getLayoutX() + getWidth() / 2 + rand.nextDouble() * scaleW,
                getLayoutY() + getHeight() / 2 + rand.nextDouble() * scaleH
            );
            node.setPosition(p);
            //			node.setCenterX(getLayoutX() + getWidth() / 2 + rand.nextDouble() * scaleW);
            //			node.setCenterY(getLayoutY() + getHeight() / 2 + rand.nextDouble() * scaleH);
        }
    }

    private double i = 0;

    public void run(double timeStep) {
        System.out.println(t);
        i += timeStep;
        if (i > maxIterations) {
            //			System.out.println("over");
            //			return;
        }
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

        Bounds b = getBoundsInLocal();
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

        //				double w = width / 2;
        //				double x = Math.min(w, Math.max(-w, v.getCenterX()));
        //				v.setCenterX(x);
        //
        //				double h = height / 2;
        //				double y = Math.min(h, Math.max(-h, v.getCenterY()));
        //				v.setCenterY(y);
    }

    private static final double BETA = 200;

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
