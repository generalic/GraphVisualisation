package hr.fer.zemris.graph.force;

import hr.fer.zemris.graph.force.constant.ForceConstant;
import java.util.List;

/**
 * Created by generalic on 7.5.2016..
 */
public interface IForce {

    void init(ForceSimulator simulator);

    String getForceName();

    List<ForceConstant> getConstants();
}
