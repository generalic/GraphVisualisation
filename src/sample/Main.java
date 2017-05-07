package sample;

import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    private static final Path PATH = Paths.get("miserables.json");

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main_screen.fxml"));
        Parent root = loader.load();

        MainScreenController controller = loader.getController();
        controller.setMainStage(primaryStage);
        controller.setGraph(PATH);

        primaryStage.setTitle("Automatic Graph Visualization");
        primaryStage.getIcons().add(new Image(getClass().getResource("hub-2-128.png").toString()));
        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
