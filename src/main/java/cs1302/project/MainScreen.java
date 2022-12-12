package cs1302.project;

import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;

/**
 * Contains the init, start, and stop methods for
 * the JavaFX application. This class extends
 * the Application class.
 */
public class MainScreen extends Application {

    Stage stage; //Stage for javafx app
    Scene scene; //Scene for javafx app
    HomeScreen hScreen; //Instance of HomeScreen class 
    HBox mainHBox; //HBox that is added to the scene

    /**
     * Constructor for this MainScreen class that initializes
     * the hScreen and mainHBox variables.
     */
    public MainScreen() {
        this.hScreen = new HomeScreen();
        this.mainHBox = new HBox();
    }

    /**
     * First method called when JavaFX application is ran.
     */
    public void init() {
        System.out.println("Executing init method");
        Platform.runLater(() -> this.mainHBox.getChildren().addAll(this.hScreen));
        Platform.runLater(() -> mainHBox.setAlignment(Pos.CENTER));
    }

    /**
     * Method is called after init method.
     * @param stage the stage variable for the JavaFX applications.
     */
    public void start(Stage stage) {
        this.stage = stage;
        this.scene = new Scene(this.mainHBox, 880, 600);
        this.stage.setOnCloseRequest(event -> Platform.exit());
        this.stage.setTitle("TravelGuru (For US Locations ONLY)");
        this.stage.setScene(this.scene);
        this.stage.sizeToScene();
        this.stage.show();
        Platform.runLater(() -> this.stage.setResizable(false));
    }

    /**
     * Method is called when user exits the JavaFX program.
     */
    public void stop() {
        System.out.println("Executing the stop method");
    }

}
