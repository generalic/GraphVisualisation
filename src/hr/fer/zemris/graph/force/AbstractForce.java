package hr.fer.zemris.graph.force;

import hr.fer.zemris.graph.force.constant.ForceConstant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by generalic on 7.5.2016..
 */
public abstract class AbstractForce implements IForce {

    private String name;

    public AbstractForce(String name) {
        this.name = name;
    }

    protected List<ForceConstant> constants = new ArrayList<>();

    @Override
    public void init(ForceSimulator simulator) {
        // do nothing
    }

    @Override
    public String getForceName() {
        return name;
    }

    public List<ForceConstant> getConstants() {
        return Collections.unmodifiableList(constants);
    }
}
