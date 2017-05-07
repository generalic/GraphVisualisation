package hr.fer.zemris.graph.force.items;

import hr.fer.zemris.graph.force.IForce;
import hr.fer.zemris.graph.node.Node;

/**
 * Created by generalic on 7.5.2016..
 */
public interface IItemForce extends IForce {

    void calculateForce(Node node);
}
