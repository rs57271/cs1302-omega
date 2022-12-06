package cs1302.project;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.net.http.HttpClient;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class HomeScreen extends VBox {

    Text text;
    TextField tField;
    Button button;

    public HomeScreen() {
        super();
        this.text = new Text("Travel Guru");
        this.tField = new TextField("Search for a city");
        this.button = new Button("Go");
        this.getChildren().addAll(text, tField, button);
    }

    // private void getInfoOfCity(ActionEvent e) {

    // }
}
