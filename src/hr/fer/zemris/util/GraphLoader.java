package hr.fer.zemris.util;

import com.google.gson.Gson;
import hr.fer.zemris.graph.test.Graph;
import hr.fer.zemris.graph.edge.Edge;
import hr.fer.zemris.graph.node.Node;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Created by generalic on 4.5.2016..
 */
public class GraphLoader {

    private Graph graph;
    private JSONGraph loader;

    private static int maxValue = 1;
    private static int minValue = 1;

    public static int getMaxValue() {
        return maxValue;
    }

    public static int getMinValue() {
        return minValue;
    }

    private Random rand = RandomProvider.get();

    public GraphLoader(String path) throws IOException {
        this(Paths.get(path));
    }

    public GraphLoader(Path path) throws IOException {
        readFile(path);
        loadGraph();
    }

    private void readFile(Path path) throws IOException {
        InputStream in = Files.newInputStream(path);

        @SuppressWarnings("resource")
        String source = new Scanner(in).useDelimiter("\\A").next();

        Gson gson = new Gson();
        this.loader = gson.fromJson(source, JSONGraph.class);
    }

    private void loadGraph() {
        maxValue =
            loader.getLinks().stream().mapToInt(JSONGraph.LinksBean::getValue).max().getAsInt();
        minValue =
            loader.getLinks().stream().mapToInt(JSONGraph.LinksBean::getValue).min().getAsInt();

        List<Node> nodes = createNodes();
        List<Edge> edges = createEdges(nodes);

        paintGroups(nodes);
        this.graph = new Graph(nodes, edges);
    }

    private List<Node> createNodes() {
        return loader.getNodes().stream()
            .map(n -> new Node(n.getName(), n.getGroup()))
            .collect(Collectors.toList());
    }

    private List<Edge> createEdges(List<Node> nodes) {
        return loader.getLinks().stream()
            .map(l -> new Edge(nodes.get(l.getSource()), nodes.get(l.getTarget()), l.getValue()))
            .collect(Collectors.toList());
    }

    private void paintGroups(List<Node> nodes) {
        long numberOfGroups = nodes.stream().map(Node::getGroup).distinct().count();

        Set<Paint> colors = new LinkedHashSet<>();
        while (colors.size() != numberOfGroups) {
            colors.add(Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
        }

        List<Paint> paints = new ArrayList<>(colors);
        IntStream.range(0, paints.size()).forEach(i -> paintGroup(nodes, i, paints.get(i)));
    }

    private void paintGroup(List<Node> nodes, int group, Paint paint) {
        nodes.stream()
            .filter(n -> n.getGroup() == group)
            .forEach(n -> n.setFill(paint));
    }

    public Graph getGraph() {
        return graph;
    }
}
