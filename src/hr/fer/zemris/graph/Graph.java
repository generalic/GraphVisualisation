package hr.fer.zemris.graph;

import hr.fer.zemris.graph.edge.Edge;
import hr.fer.zemris.graph.node.Node;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Graph {

    private List<Node> nodes;
    private List<Edge> edges;

    public Graph(Collection<Node> nodes, Collection<Edge> edges) {
        super();
        this.nodes = new ArrayList<>(nodes);
        this.edges = new ArrayList<>(edges);
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }
}
