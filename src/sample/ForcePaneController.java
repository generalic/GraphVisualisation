package sample;

import hr.fer.zemris.graph.force.IForce;
import hr.fer.zemris.graph.force.constant.ForceConstant;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;

public class ForcePaneController {

    private static final String FORCE_CONSTANT_FXML = "force_constant.fxml";

    @FXML
    private TitledPane forcePane;

    @FXML
    private GridPane forceGrid;

    private IForce force;

    public void setForce(IForce force) throws IOException {
        this.force = force;
        init();
    }

    private void init() throws IOException {
        forcePane.setText(force.getForceName());

        for (int i = 0, n = force.getConstants().size(); i < n; i++) {
            ForceConstant constant = force.getConstants().get(i);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FORCE_CONSTANT_FXML));
            Parent forceConstantLabel = loader.load();

            ForceConstantController forceConstantController = loader.getController();
            forceConstantController.setConstant(constant);

            forceGrid.addRow(i, forceConstantLabel);
        }
    }
}
