package cs1302.project;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;

public class MainScreen extends Application {

    Stage stage;
    Scene scene;
    HomeScreen hScreen;
    HBox mainHBox;

    public MainScreen() {
        this.hScreen = new HomeScreen();
        this.mainHBox = new HBox();
    }

    public void init() {
        System.out.println("Executing init method");
        Platform.runLater(() -> this.mainHBox.getChildren().addAll(this.hScreen));
        Platform.runLater(() -> mainHBox.setAlignment(Pos.CENTER));
    }

    public void start(Stage stage) {
        this.stage = stage;
        this.scene = new Scene(this.mainHBox, 1280, 700);
        this.stage.setOnCloseRequest(event -> Platform.exit());
        this.stage.setTitle("TravelGuru");
        this.stage.setScene(this.scene);
        this.stage.sizeToScene();
        this.stage.show();
        Platform.runLater(() -> this.stage.setResizable(false));
    }

    public void stop() {
        System.out.println("Executing the stop method");
    }

}
