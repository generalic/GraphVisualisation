package hr.fer.zemris.util;

import hr.fer.zemris.graph.Graph;
import hr.fer.zemris.graph.edge.Edge;
import hr.fer.zemris.graph.node.Node;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Created by generalic on 11.5.2016..
 */
public class JSONGenerator {

    public static void generate(Graph graph) {
        List<Node> nodes = graph.getNodes();
        List<Edge> edges = graph.getEdges();

        StringBuilder sb = new StringBuilder();

        sb.append("{\n");
        sb.append("\t\"nodes\":[\n");

        for (Node n : nodes) {
            sb.append("\t\t{\"name\":\"" + n.getName() + "\",\"group\":" + n.getGroup() + "},\n");
        }
        sb.deleteCharAt(sb.length() - 2);

        sb.append("\t],\n");
        sb.append("\t\"links\":[\n");

        for (Edge e : edges) {
            int i = nodes.indexOf(e.getSource());
            int j = nodes.indexOf(e.getTarget());
            int v = (int) e.getValue();
            sb.append("\t\t{\"source\":" + i + ",\"target\":" + j + ",\"value\":" + v + "},\n");
        }
        sb.deleteCharAt(sb.length() - 2);

        sb.append("\t]\n");
        sb.append("}");

        System.out.println(new String(sb.toString().getBytes(StandardCharsets.UTF_8)));
    }
}
