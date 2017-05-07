package hr.fer.zemris.graph.force.intergrator;

import hr.fer.zemris.graph.force.ForceSimulator;

/**
 * Created by generalic on 7.5.2016..
 */
public interface Integrator {

    void integrate(ForceSimulator simulator, double timeStep);
}
