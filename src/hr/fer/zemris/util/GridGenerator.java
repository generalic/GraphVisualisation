package hr.fer.zemris.util;

import hr.fer.zemris.graph.Graph;
import hr.fer.zemris.graph.edge.Edge;
import hr.fer.zemris.graph.node.Node;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * Created by generalic on 9.5.2016..
 */
public class GridGenerator {

    private static final int N = 10;

    private List<Node> nodes = new ArrayList<>();
    private Set<Edge> edges = new LinkedHashSet<>();

    private Graph graph;

    public GridGenerator() {
        generate();
        graph = new Graph(nodes, edges);
    }

    public Graph generateGraph() {
        return graph;
    }

    private void generate() {
        IntStream.range(0, N * N)
            .boxed()
            .map(i -> new Node(Integer.toString(i)))
            .forEach(nodes::add);

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                createEdges(i, j);
            }
        }
    }

    private void createEdges(int i, int j) {
        List<Tuple> tuples = Arrays.asList(
            new Tuple(i, j + 1),
            new Tuple(i + 1, j)
        );
        Tuple source = new Tuple(i, j);
        tuples.stream()
            .filter(this::isValidTuple)
            .map(t -> createEdge(source, t))
            .forEach(edges::add);
    }

    private Edge createEdge(Tuple s, Tuple t) {
        return new Edge(nodes.get(s.x * N + s.y), nodes.get(t.x * N + t.y));
    }

    private boolean isValidTuple(Tuple t) {
        return isValidIndex(t.x) && isValidIndex(t.y);
    }

    private boolean isValidIndex(int index) {
        return !(index < 0 || index >= N);
    }

    private static class Tuple {

        private int x;
        private int y;

        public Tuple(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Tuple tuple = (Tuple) o;

            if (x != tuple.x) return false;
            return y == tuple.y;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            return result;
        }
    }

    public static void main(String[] args) {
        GridGenerator g = new GridGenerator();

        JSONGenerator.generate(g.graph);
    }
}
