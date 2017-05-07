package hr.fer.zemris.util;

import hr.fer.zemris.graph.test.Graph;
import hr.fer.zemris.graph.edge.Edge;
import hr.fer.zemris.graph.node.Node;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A clique is a graph in which every node
 * is a neighbor of every other node.
 *
 * Created by generalic on 9.5.2016..
 */
public class HoneycombGenerator {

    private static final int DEFAULT_N = 10;

    private int n;

    private Set<Node> nodes = new LinkedHashSet<>();
    private Set<Edge> edges = new LinkedHashSet<>();

    private Graph graph;

    public HoneycombGenerator() {
        this(DEFAULT_N);
    }

    public HoneycombGenerator(final int n) {
        this.n = n;
        generate();
        graph = new Graph(nodes, edges);
    }

    public Graph generateGraph() {
        return graph;
    }

    public void getHoneycomb() {
        ArrayList<Node> layer1 = halfcomb(n);
        ArrayList<Node> layer2 = halfcomb(n);
        for (int i = 0; i < (n << 1); ++i) {
            Node n1 = layer1.get(i);
            Node n2 = layer2.get(i);
            edges.add(new Edge(n1, n2));
        }
    }

    private int index = 0;

    private ArrayList<Node> halfcomb(int levels) {
        ArrayList<Node> top = new ArrayList<>();
        ArrayList<Node> layer = new ArrayList<>();

        int label = 0;

        for (int i = 0; i < levels; ++i) {
            Node n = new Node(String.valueOf(index++));
            nodes.add(n);
            top.add(n);
        }

        for (int i = 0; i < levels; ++i) {
            Node n = null;
            for (int j = 0; j < top.size(); ++j) {
                Node p = top.get(j);
                if (n == null) {
                    n = new Node(String.valueOf(index++));
                    nodes.add(n);
                    layer.add(n);
                }
                edges.add(new Edge(p, n));
                n = new Node(String.valueOf(index++));
                nodes.add(n);
                layer.add(n);
                edges.add(new Edge(p, n));
            }
            if (i == levels - 1) {
                return layer;
            }
            top.clear();
            for (int j = 0; j < layer.size(); ++j) {
                Node p = layer.get(j);
                n = new Node(String.valueOf(index++));
                nodes.add(n);
                top.add(n);
                edges.add(new Edge(p, n));
            }
            layer.clear();
        }
        // should never happen
        return top;
    }

    private void generate() {
        getHoneycomb();
    }

    public static void main(String[] args) {
        HoneycombGenerator g = new HoneycombGenerator(2);
        JSONGenerator.generate(g.graph);
    }
}
