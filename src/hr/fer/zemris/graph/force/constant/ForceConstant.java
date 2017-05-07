package hr.fer.zemris.graph.force.constant;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by generalic on 17.5.2016..
 */
public class ForceConstant {

    private StringProperty name;

    private DoubleProperty value;
    private DoubleProperty minValue;
    private DoubleProperty maxValue;

    public ForceConstant(String name, double value, double minValue, double maxValue) {
        this.name = new SimpleStringProperty(name);
        this.value = new SimpleDoubleProperty(value);
        this.minValue = new SimpleDoubleProperty(minValue);
        this.maxValue = new SimpleDoubleProperty(maxValue);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public double getValue() {
        return value.get();
    }

    public DoubleProperty valueProperty() {
        return value;
    }

    public double getMinValue() {
        return minValue.get();
    }

    public DoubleProperty minValueProperty() {
        return minValue;
    }

    public double getMaxValue() {
        return maxValue.get();
    }

    public DoubleProperty maxValueProperty() {
        return maxValue;
    }
}
