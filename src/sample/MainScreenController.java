package sample;

import hr.fer.zemris.graph.test.Graph;
import hr.fer.zemris.graph.test.GraphPane;
import hr.fer.zemris.graph.force.ForceSimulator;
import hr.fer.zemris.graph.force.IForce;
import hr.fer.zemris.graph.layout.ForceDirectedLayout;
import hr.fer.zemris.util.GraphLoader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainScreenController {

    private static final String FORCE_PANE_FXML = "force_pane.fxml";

    @FXML
    private BorderPane centerPane;

    @FXML
    private Accordion forceAccordion;

    @FXML
    private MenuItem openFileMenuItem;

    @FXML
    private MenuItem resetFileMenuItem;

    @FXML
    private Slider frameSlider;

    @FXML
    private Label frameSliderValueLabel;

    @FXML
    private ToggleButton initConfigToggle;

    private ForceDirectedLayout fda;

    private Stage mainStage;

    private Path path;

    private AnimationTimer timer;

    private IntegerProperty rate = new SimpleIntegerProperty(10);

    @FXML
    public void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Text Files", "*.json"),
            new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(mainStage);
        if (selectedFile != null) {
            try {
                clear();
                setGraph(selectedFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void resetFile() throws IOException {
        clear();
        setGraph(path);
    }

    @FXML
    public void showAboutWindow() {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        StackPane dialogVbox = new StackPane();
        dialogVbox.getChildren().add(new Text(
            "Automatic Graph Visualization\n" +
                "Author: Boris Generalić\n" +
                "Mentor: doc.dr.sc. Marko Čupić"
        ));
        Scene dialogScene = new Scene(dialogVbox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void clear() {
        timer.stop();
        forcePanes.forEach(p -> forceAccordion.getPanes().remove(p));
        forcePanes.clear();
        frameSlider.valueProperty().unbindBidirectional(rate);
    }

    public void setGraph(Path path) throws IOException {
        this.path = path;

        GraphLoader graphLoader = new GraphLoader(path);
        Graph graph = graphLoader.getGraph();

        graph.getEdges()
            .forEach(e -> e.setValue(e.getValue(), GraphLoader.getMinValue(),
                GraphLoader.getMaxValue()));

        this.fda = new ForceDirectedLayout(graph, 1200, 700);
        fda.centerPane = centerPane;
        initConfigToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            initConfigToggle.setText(newValue ? "Random" : "Simulated Annealing");
            fda.initConfigProperty().setValue(newValue);
        });
        init();

        GraphPane graphPane = new GraphPane(fda);
        setGraphPane(graphPane.getRoot());

        frameSlider.valueProperty().bindBidirectional(rate);
        frameSliderValueLabel.textProperty().bind(frameSlider.valueProperty().asString("%.1f"));

        //		try {
        //			Thread.sleep(1500);
        //		} catch (InterruptedException e) {
        //			e.printStackTrace();
        //		}

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                fda.run(rate.get());
            }
        };
        timer.start();
    }

    public void setGraphPane(AnchorPane graphPane) {
        centerPane.setCenter(graphPane);
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    private List<TitledPane> forcePanes = new ArrayList<>();

    private void init() throws IOException {
        ForceSimulator simulator = fda.getSimulator();

        for (IForce force : simulator.getForces()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FORCE_PANE_FXML));
            TitledPane forcePane = loader.load();

            ForcePaneController forcePaneController = loader.getController();

            forcePaneController.setForce(force);

            forcePanes.add(forcePane);
            forceAccordion.getPanes().add(forcePane);
        }
    }
}
