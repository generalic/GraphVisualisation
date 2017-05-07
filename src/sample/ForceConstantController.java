package sample;

import hr.fer.zemris.graph.force.constant.ForceConstant;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;

public class ForceConstantController {

    @FXML
    GridPane forceConstantControl;

    @FXML
    Label nameLabel;

    @FXML
    Slider slider;

    @FXML
    Label valueLabel;

    private ForceConstant constant;

    public void setConstant(ForceConstant constant) {
        this.constant = constant;
        init();
    }

    private void init() {
        nameLabel.textProperty().bind(constant.nameProperty());

        // TODO
        //		if(constant.getName().equals("Distance")) {
        //			slider.valueProperty().addListener((observable, oldValue, newValue) -> {
        //				System.out.println(oldValue);
        //				System.out.println(newValue);
        //			});
        //		}

        slider.valueProperty().bind(constant.valueProperty());

        slider.valueProperty().unbind();

        constant.valueProperty().bind(slider.valueProperty());

        slider.minProperty().bind(constant.minValueProperty());
        slider.maxProperty().bind(constant.maxValueProperty());

        valueLabel.textProperty().bind(slider.valueProperty().asString("%.5f"));
    }
}
