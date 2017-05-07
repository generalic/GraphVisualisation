package hr.fer.zemris.graph.test;

import hr.fer.zemris.graph.edge.Edge;
import hr.fer.zemris.graph.node.Node;
import javafx.application.Application;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

class Test extends AnchorPane {
    private Rectangle rect;
    public double pressedX;
    public double pressedY;
    private LongProperty frame = new SimpleLongProperty();

    public Test() {

        setMinSize(600, 600);
        setStyle("-fx-background-color: springgreen");
        //		Label count = new Label();
        //		count.textProperty().bind(Bindings.convert(frame));
        //		getChildren().add(count);
        //		count.setMouseTransparent(true);

        //		setOnMousePressed(new EventHandler<MouseEvent>() {
        //			public void handle(MouseEvent event) {
        //				pressedX = event.getX();
        //				pressedY = event.getY();
        //			}
        //		});
        //
        ////		addEventFilter(MouseEvent.ANY,
        ////				new EventHandler<MouseEvent>() {
        ////					public void handle(final MouseEvent mouseEvent) {
        ////						mouseEvent.consume();
        ////					}
        ////				});
        //
        //
        //		setOnMouseDragged(new EventHandler<MouseEvent>() {
        //			public void handle(MouseEvent event) {
        //				setTranslateX(getTranslateX() + event.getX() - pressedX);
        //				setTranslateY(getTranslateY() + event.getY() - pressedY);
        //
        //				event.consume();
        //			}
        //		});

        //		Timeline t = new Timeline(new KeyFrame(Duration.millis(100), new EventHandler<ActionEvent>() {
        //			@Override public void handle(ActionEvent event) {
        //				frame.set(frame.get() + 1);
        //
        //				if (rect != null) {
        //					getChildren().remove(rect);
        //				}
        //
        //				rect = new Rectangle(10, 10, 200, 200);
        //				rect.setFill(Color.RED);
        //				rect.setMouseTransparent(true);
        //				getChildren().add(0, rect);
        //			}
        //		}));
        //		t.setCycleCount(Timeline.INDEFINITE);
        //		t.play();
    }
}

public class TestApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Test pane = new Test();

        Node n1 = new Node("ivica", 3);
        n1.setCenterX(100);
        n1.setCenterY(400);
        n1.setFill(Paint.valueOf("#ffffff"));

        Node n2 = new Node("braco", 3);
        n2.setCenterX(700);
        n2.setCenterY(400);
        n2.setFill(Paint.valueOf("#f83838"));

        Edge e1 = new Edge(n1, n2, 1);
        e1.setStrokeWidth(1);
        e1.setStroke(Paint.valueOf("ffffff"));

        Canvas canvas = new Canvas(1200, 700);

        pane.getChildren().addAll(n1, n2, e1);

        //		Group root = new Group(pane, n1, n2, e1);

        Scene scene = new Scene(pane);

        scene.setOnMouseClicked(e -> {
            pane.pressedX = e.getX();
            pane.pressedY = e.getY();
        });
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                pane.pressedX = event.getX();
                pane.pressedY = event.getY();
            }
        });

        //		addEventFilter(MouseEvent.ANY,
        //				new EventHandler<MouseEvent>() {
        //					public void handle(final MouseEvent mouseEvent) {
        //						mouseEvent.consume();
        //					}
        //				});

        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                pane.setTranslateX(pane.getTranslateX() + event.getX());
                pane.setTranslateY(pane.getTranslateY() + event.getY());

                event.consume();
            }
        });

        stage.setScene(scene);
        stage.show();
    }
}
