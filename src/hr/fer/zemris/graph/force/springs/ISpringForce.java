package hr.fer.zemris.graph.force.springs;

import hr.fer.zemris.graph.edge.Edge;
import hr.fer.zemris.graph.force.IForce;

/**
 * Created by generalic on 7.5.2016..
 */
public interface ISpringForce extends IForce {

    void calculateForce(Edge edge);
}
