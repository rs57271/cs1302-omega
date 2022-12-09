package cs1302.project;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class HomeScreen extends VBox {

    static Text text;
    static TextField tField;
    static Button button;
    static HBox content;
    static VBox titleForWeatherVBox;
    static VBox titleForAttractionsVBox;
    static VBox weatherVBox;
    static VBox attractionsVBox;

    /** HTTP client. */
    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2) // uses HTTP protocol version 2 where possible
            .followRedirects(HttpClient.Redirect.NORMAL) // always redirects, except from HTTPS to HTTP
            .build(); // builds and returns a HttpClient object

    /** Google {@code Gson} object for parsing JSON-formatted strings. */
    public static Gson GSON = new GsonBuilder()
            .setPrettyPrinting() // enable nice output when printing
            .create();

    public static class coordinatesOfSpecifiedLocation {
        String name;
        String country;
        String lat;
        String lon;
        String population;
        String timezone;
        String status;
    }

    public static class TopLevelAttractionList {
        String type;
        AttractionList[] features;
    }

    public static class AttractionList {
        String type;
        String id;
        Geometry geometry;
        Properties properties;
    }

    public static class Geometry {
        String type;
        String[] coordinates;
    }

    public static class Properties {
        String xid;
        String name;
        String dist;
        String rate;
        String wikidata;
        String kinds;
    }

    public HomeScreen() {
        super(30);
        this.text = new Text("Travel Guru");
        this.text.setFont(new Font(40));
        this.tField = new TextField("Search for a city");
        this.button = new Button("Go");
        this.weatherVBox = new VBox(15);
        this.attractionsVBox = new VBox(15);
        this.titleForAttractionsVBox = new VBox();
        this.titleForWeatherVBox = new VBox();
        titleForAttractionsVBox.setMaxWidth(800);
        titleForAttractionsVBox.setMaxHeight(200);
        titleForWeatherVBox.setMaxWidth(800);
        titleForWeatherVBox.setMaxHeight(200);
        titleForAttractionsVBox.setPrefWidth(800);
        titleForAttractionsVBox.setPrefHeight(200);
        titleForWeatherVBox.setPrefWidth(800);
        titleForWeatherVBox.setPrefHeight(200);
        this.titleForWeatherVBox = new VBox();
        this.tField.setPrefWidth(400);
        this.tField.setMaxWidth(400);
        Platform.runLater(() -> titleForWeatherVBox.getChildren().addAll(new Text("Current weather: "), weatherVBox));
        Platform.runLater(() -> titleForAttractionsVBox.getChildren().addAll(new Text("3 cool places to check out: "),
                attractionsVBox));
        Platform.runLater(() -> weatherVBox.setAlignment(Pos.BASELINE_LEFT));
        Platform.runLater(() -> attractionsVBox.setAlignment(Pos.BASELINE_LEFT));
        Platform.runLater(
                () -> this.getChildren().addAll(text, tField, button, titleForWeatherVBox, titleForAttractionsVBox));
        Platform.runLater(() -> this.setAlignment(Pos.BASELINE_CENTER));
        Platform.runLater(() -> this.button.setOnAction(e -> getInfoOfCity(e)));
    }

    private static void getInfoOfCity(ActionEvent e) {
        String json = "";
        String json2 = "";
        String lon = "";
        String lat = "";
        String tFieldText = tField.getText();
        attractionsVBox.getChildren().clear();
        weatherVBox.getChildren().clear();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            "https://opentripmap-places-v1.p.rapidapi.com/en/places/geoname?name=" + tFieldText))
                    .header("X-RapidAPI-Key", "c3ac180905msh73f17d19552d9fbp18596fjsndbedb539c487")
                    .header("X-RapidAPI-Host", "opentripmap-places-v1.p.rapidapi.com")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request,
                    HttpResponse.BodyHandlers.ofString());
            json = response.body();
            coordinatesOfSpecifiedLocation result = GSON.fromJson(json, coordinatesOfSpecifiedLocation.class);
            lon = result.lon;
            lat = result.lat;
        } catch (Exception exc) {
            System.out.println("There was an exception!");
            System.out.println(exc);
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://opentripmap-places-v1.p.rapidapi.com/en/places/radius?radius=500&lon="
                            + lon + "&lat=" + lat + "&rate=3&limit=20"))
                    .header("X-RapidAPI-Key",
                            "3591847edamsh482247611f3042ap1ff135jsn1acaa240a433")
                    .header("X-RapidAPI-Host", "opentripmap-places-v1.p.rapidapi.com")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request,
                    HttpResponse.BodyHandlers.ofString());
            json2 = response.body();
            TopLevelAttractionList result2 = GSON.fromJson(json2, TopLevelAttractionList.class);
            int i = 0;
            for (AttractionList al : result2.features) {
                Text t = new Text(
                        "Name: " + al.properties.name + "\n" + "Rate: " + al.properties.rate + "\n" + "Type:"
                                + al.properties.kinds);
                Platform.runLater(() -> attractionsVBox.getChildren().add(t));
                if (i == 2) {
                    break;
                } else {
                    i++;
                }
            }
        } catch (Exception exc) {
            System.out.println("There was an exception!");
            System.out.println(exc);
        }

    }
}
