package hr.fer.zemris.graph.force.intergrator;

import hr.fer.zemris.graph.force.ForceSimulator;
import hr.fer.zemris.graph.node.Node;

/**
 * Created by generalic on 15.5.2016..
 */
public class EulerIntegrator implements Integrator {

    @Override
    public void integrate(ForceSimulator simulator, double timeStep) {
        double speedLimit = simulator.getSpeedLimit();

        for (Node item : simulator.getNodes()) {
            item.setPosition(item.getVelocity().multiply(timeStep));

            double coeff = timeStep / item.mass;
            item.setVelocity(item.getVelocity().add(item.getForce().multiply(coeff)));

            double v = item.getVelocity().magnitude();
            if (v > speedLimit) {
                item.setVelocity(item.getVelocity().multiply(speedLimit / v));
            }
        }
    }
}
