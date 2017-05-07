package hr.fer.zemris.graph.force.intergrator;

import hr.fer.zemris.graph.force.ForceSimulator;
import hr.fer.zemris.graph.node.Node;
import javafx.geometry.Point2D;

/**
 * Updates velocity and position data using the 4th-Order Runge-Kutta method.
 * It is slower but more accurate than other techniques such as Euler's Method.
 * The technique requires re-evaluating forces 4 times for a given timestep.
 *
 * Created by generalic on 7.5.2016..
 */
public class RungeKuttaIntegrator implements Integrator {

    @Override
    public void integrate(ForceSimulator simulator, double timeStep) {
        double speedLimit = simulator.getSpeedLimit();
        Point2D velocity;
        double v;
        double coefficient;
        Point2D[] k, l;

        for (Node node : simulator.getNodes()) {
            coefficient = timeStep / node.mass;
            k = node.k;
            l = node.l;

            node.setPreviousPosition(node.getPosition());
            k[0] = node.getVelocity().multiply(timeStep);
            l[0] = node.getForce().multiply(coefficient);

            // Set the position to the new predicted position
            node.setPosition(node.getPosition().add(k[0].multiply(0.5d)));
        }

        // recalculate forces
        simulator.accumulate();

        for (Node node : simulator.getNodes()) {
            coefficient = timeStep / node.mass;
            k = node.k;
            l = node.l;

            velocity = node.velocity.add(l[0].multiply(0.5d));
            v = velocity.magnitude();
            if (v > speedLimit) {
                velocity = velocity.multiply(speedLimit / v);
            }

            k[1] = velocity.multiply(timeStep);
            l[1] = node.getForce().multiply(coefficient);

            // Set the position to the new predicted position
            node.setPosition(node.getPreviousPosition().add(k[1].multiply(0.5d)));
        }

        // recalculate forces
        simulator.accumulate();

        for (Node node : simulator.getNodes()) {
            coefficient = timeStep / node.mass;
            k = node.k;
            l = node.l;

            velocity = node.getVelocity().add(l[0].multiply(0.5d));
            v = velocity.magnitude();
            if (v > speedLimit) {
                velocity = velocity.multiply(speedLimit / v);
            }

            k[2] = velocity.multiply(timeStep);
            l[2] = node.getForce().multiply(coefficient);

            // Set the position to the new predicted position
            node.setPosition(node.getPreviousPosition().add(k[2].multiply(0.5d)));
        }

        // recalculate forces
        simulator.accumulate();

        for (Node node : simulator.getNodes()) {
            coefficient = timeStep / node.getMass();
            k = node.k;
            l = node.l;
            Point2D p = node.getPreviousPosition();

            velocity = node.getVelocity().add(l[2]);
            v = velocity.magnitude();
            if (v > speedLimit) {
                velocity = velocity.multiply(speedLimit / v);
            }

            k[3] = velocity.multiply(timeStep);
            l[3] = node.getForce().multiply(coefficient);

            Point2D newLocation = p
                .add(k[0].add(k[3]).multiply(1 / 6.0d))
                .add(k[1].add(k[2]).multiply(1 / 3.0d));
            node.setPosition(newLocation);

            velocity = Point2D.ZERO
                .add(l[0].add(l[3]).multiply(1 / 6.0d))
                .add(l[1].add(l[2]).multiply(1 / 3.0d));
            v = velocity.magnitude();
            if (v > speedLimit) {
                velocity = velocity.multiply(speedLimit / v);
            }

            node.setVelocity(node.getVelocity().add(velocity));
        }
    }
}
