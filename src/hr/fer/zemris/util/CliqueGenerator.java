package hr.fer.zemris.util;

import hr.fer.zemris.graph.test.Graph;
import hr.fer.zemris.graph.edge.Edge;
import hr.fer.zemris.graph.node.Node;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * A clique is a graph in which every node
 * is a neighbor of every other node.
 *
 * Created by generalic on 9.5.2016..
 */
public class CliqueGenerator {

    private static final int DEFAULT_N = 10;

    private int n;

    private List<Node> nodes = new ArrayList<>();
    private Set<Edge> edges = new LinkedHashSet<>();

    private Graph graph;

    public CliqueGenerator() {
        this(DEFAULT_N);
    }

    public CliqueGenerator(final int n) {
        this.n = n;
        generate();
        graph = new Graph(nodes, edges);
    }

    public Graph generateGraph() {
        return graph;
    }

    private void generate() {
        IntStream.range(0, n)
            .boxed()
            .map(i -> new Node(Integer.toString(i)))
            .forEach(nodes::add);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    edges.add(new Edge(nodes.get(i), nodes.get(j)));
                }
            }
        }
    }

    public static void main(String[] args) {
        CliqueGenerator g = new CliqueGenerator(11);
        JSONGenerator.generate(g.graph);
    }
}
