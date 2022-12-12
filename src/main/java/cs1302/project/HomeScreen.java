package cs1302.project;

import javafx.scene.control.Button;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * This class extends {@class VBox} and contains all the JavaFX
 * elements that the user will see. This class also calls the Places API,
 * Dark-sky API, and Meteostat APIs. The class returns information about
 * the weather and attractions in a particular city.
 */
public class HomeScreen extends VBox {

    Text titleForApp; //Contains the title for the app (Travel Guru).
    TextField tField; //Text field where user types the city.
    Button button; //Go button which user clicks to query the apis.
    VBox contentVBox; //VBox contains all the information recieved from the apis.
    Text titleForWeather; //Text that says Weather info:.
    Text titleForAttractions; //Text that says 3 attractions to check out:.
    HBox searchBox; //HBox contains text field and go button.
    Text instructions; //Contains instructions for the user to follow in the application.

    /** HTTP client. */
    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2) // uses HTTP protocol version 2 where possible
            .followRedirects(HttpClient.Redirect.NORMAL) // always redirects, except from HTTPS
                                                        // to HTTP
            .build(); // builds and returns a HttpClient object

    /** Google {@code Gson} object for parsing JSON-formatted strings. */
    public static Gson GSON = new GsonBuilder()
            .setPrettyPrinting() // enable nice output when printing
            .create();

    /**
     * This class models the json returned from the Places API. Contains
     * the longitutude and latitude variables that equals to the latitudes
     * and longitudes of a particular city.
     */
    public class CoordinatesOfSpecifiedLocation {
        String lat;
        String lon;
    }

    /**
     * This class models the json returned from the Places API when the
     * latitudes and longitudes are provided. JSON returns an
     * array called features that contains various information about
     * attractions in a particular city.
     */
    public class TopLevelAttractionList {
        AttractionList[] features;
    }

    /**
     * This class models the json returned from the Places API when the
     * latitudes and longitudes are provided. JSON returns attractions a
     * class called properties that contains information about one attraction.
     */
    public class AttractionList {
        Properties properties;
    }

    /**
     * This class models the json returned from the Places API when the
     * latitudes and longitudes are provided. JSON returns this class and it
     * contains the name and rating of each attraction.
     */
    public class Properties {
        String name;
        String rate;
        String kinds;
    }

    /**
     * This class models the json returned from the meteostat API when the longitudes
     * and latitudes are provided to return nearest weather stations to those
     * coordinates. Class contains an array called data that has information about
     * each station.
     */
    public class Stations {
        Data[] data;
    }

    /**
     * This class models the json returned from the meteostat API when the longitudes
     * and latitudes are provided to return nearest weather stations to those
     * coordinates. This class contains data of each station, such as its id and name.
     */
    public class Data {
        String id;
    }

    /**
     * This class models the json returned from the DarkSky API when the longititudes
     * and latitudes are provided to return the current weather in that location.
     */
    public class CurrentWeather {
        WeatherNow currently;
    }

    /**
     * This class models the json returned from the DarkSky API when the longititudes
     * and latitudes are provided to return the current precipiation probability
     * and temperature in that location.
     */
    public class WeatherNow {
        String precipProbability;
        String temperature;
    }

    /**
     * This class models the json returned from the meteostat api when the station
     * id is provided. Returns an array called data that contains the weather
     * data in a location for multiple dates.
     */
    public class HistoricWeather {
        DataOfTemps[] data;
    }

    /**
     * This class models the json returned from the meteostat api when the station
     * id is provided. This class contains the date and average temperature on a
     * particular location on a particular day.
     */
    public class DataOfTemps {
        String date;
        String tavg;
    }

    /**
     * Constructor for {@class HomeScreen} that initializes the javaFX
     * elements.
     */
    public HomeScreen() {
        super(20);
        titleForApp = new Text("Travel Guru for US locations ONLY");
        titleForApp.setFont(new Font(40));
        titleForWeather = new Text("Weather info: \n");
        titleForAttractions = new Text("3 attractions to check out: \n ");
        titleForAttractions.setFont(new Font(20));
        titleForWeather.setFont(new Font(20));
        instructions = new Text("Type: City name, State, Country." +
        "For example: Athens, GA, USA or Athens, Georgia, USA.");
        tField = new TextField("Search for a city");
        button = new Button("Go");
        searchBox = new HBox(10);
        searchBox.getChildren().addAll(tField, button);
        contentVBox = new VBox();
        contentVBox.setMaxSize(800, 50);
        tField.setPrefWidth(400);
        tField.setMaxWidth(400);
        this.getChildren().addAll(titleForApp, searchBox, instructions);
        Thread t1 = new Thread(() -> button.setOnAction(e -> getInfoOfCity(e)));
        t1.start();
    }

    /**
     * This method is called when the go button is pressed. In it, it first calls
     * the places API and gets the longitude and latitude of a inputted city. 
     * @param e ActionEvent object that is called when the go button is clicked.
     */
    public void getInfoOfCity(ActionEvent e) {
        String json = "";
        String lon = "";
        String lat = "";
        String tFieldText = tField.getText();
        Platform.runLater(() -> this.contentVBox.getChildren().clear());
        Platform.runLater(() -> this.contentVBox.getChildren().add(titleForAttractions));
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            "https://opentripmap-places-v1.p.rapidapi.com/en/places/geoname?name="
                                    + URLEncoder.encode(tFieldText, StandardCharsets.UTF_8)))
                    .header("X-RapidAPI-Key", "c3ac180905msh73f17d19552d9fbp18596fjsndbedb539c487")
                    .header("X-RapidAPI-Host", "opentripmap-places-v1.p.rapidapi.com")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request,
                    HttpResponse.BodyHandlers.ofString());
            json = response.body();
            CoordinatesOfSpecifiedLocation result =
                    GSON.fromJson(json, CoordinatesOfSpecifiedLocation.class);
            lon = result.lon;
            lat = result.lat;
            getAttractions(lon,lat);
        } catch (Exception exc) {
            System.out.println("There was an exception!");
            System.out.println(exc);
        } //try-catch that gets longitudes and latitudes of an inputted city
    }
    
    /**
     * This method, uses the longitudes and latitudes found in the first method
     * to find 3 cool attractions near those coordinates.
     * @param lon the longitude found from the first method.
     * @param lat the latitude found from the first method.
     */
    public void getAttractions(String lon, String lat) {
        String json2 = "";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://opentripmap-places-v1.p.rapidapi.com/en/places/"
                    + "radius?radius=500&lon="
                    + lon + "&lat=" + lat + "&rate=3&limit=20"))
                    .header("X-RapidAPI-Key",
                            "3591847edamsh482247611f3042ap1ff135jsn1acaa240a433")
                    .header("X-RapidAPI-Host", "opentripmap-places-v1.p.rapidapi.com")
                    .GET()
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request,
                    HttpResponse.BodyHandlers.ofString());
            json2 = response.body();
            TopLevelAttractionList result2 = GSON.fromJson(json2, TopLevelAttractionList.class);
            if (result2.features.length == 0) {
                Text t = new Text("No cool places to check out here :( \n");
                Platform.runLater(() -> this.contentVBox.getChildren().add(t));
            } else {
                int i = 0;
                String[] attractions = new String[3];
                for (AttractionList al : result2.features) {
                    String s = "Name: " + al.properties.name + "\n" + "Rating: "
                        + al.properties.rate + "/10\n" + "Type: " + al.properties.kinds + "\n";
                    attractions[i] = s;
                    if (i == 2) {
                        break;
                    } else {
                        i++;
                    }
                }
                for (int j = 0; j < attractions.length; j++) {
                    Text t = new Text(attractions[j]);
                    Platform.runLater(() -> this.contentVBox.getChildren().add(t));
                }
            } //if-else that first checks whether API returns any cool
              //attractions near a location. If not, program adds text
              //says No cool places to check out. Otherwise, a for loop
              //is executed which adds 3 locations to the gui.
        } catch (Exception exc) {
            System.out.println("There was an exception!");
            System.out.println(exc);
        } //try-catch that gets 3 cool attractions near the coordinates
          //found in the first try-catch.
        getWeatherOfSpecifiedLocation(lon, lat); //First api queries second api here.
    }   

    /**
     * This method is called at the end of the first method. Inside, it calls
     * the dark-sky and meteostat apis that returns the current temperature,
     * precipation chance, and historic weather data for a particular
     * coordinate.
     * @param lon the longitude of a particular place.
     * @param lat the longitude of a particular place.
     */
    public void getWeatherOfSpecifiedLocation(String lon, String lat) {
        String jsonForStation = "";
        String stationId = "";
        String jsonForWeather = "";
        Platform.runLater(() -> this.contentVBox.getChildren().add(titleForWeather));
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://dark-sky.p.rapidapi.com/" + lat + ","
                + lon + "?units=auto&lang=en"))
                .header("X-RapidAPI-Key", "3591847edamsh482247611f3042ap1ff135jsn1acaa240a433")
                .header("X-RapidAPI-Host", "dark-sky.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
            HttpResponse<String> response =
                HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            jsonForWeather = response.body();
            CurrentWeather result = GSON.fromJson(jsonForWeather, CurrentWeather.class);
            String precipitation = "The chance of rain is: "
                + result.currently.precipProbability + "% \n";
            String weather = "Currently the temperature is: "
                + result.currently.temperature + "°F\n";
            Platform.runLater(() -> this.contentVBox.getChildren().add(new Text(weather)));
            Platform.runLater(() -> this.contentVBox.getChildren().add(new Text(precipitation)));
        } catch (Exception exc) {
            System.out.println("There was an exception!");
            System.out.println(exc);
        } //try-catch that calls the dark-sky api and returns
          //the current precipation probablity and temperature
          //in a location.
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://meteostat.p.rapidapi.com/stations/nearby?lat="
                    + lat + "&lon=" + lon))
                    .header("X-RapidAPI-Key", "3591847edamsh482247611f3042ap1ff135jsn1acaa240a433")
                    .header("X-RapidAPI-Host", "meteostat.p.rapidapi.com")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request,
                    HttpResponse.BodyHandlers.ofString());
            jsonForStation = response.body();
            Stations result = GSON.fromJson(jsonForStation, Stations.class);
            stationId = result.data[0].id;
            getWeatherNearStation(stationId);
        } catch (Exception exc) {
            System.out.println("There was an exception!");
            System.out.println(exc);
        } //try-catch that calls the meteostat api and inputs
          //the longitude and latitude data into the uri. Returns
          //the nearest weather station to those coordinates.
    }

    /**
     * This method calls the meteostat api and returns historic weather data 
     * for a particular location using the stationId found from the previous
     * method.
     * @param stationId stationId found from the previous method.
     */
    public void getWeatherNearStation(String stationId) {
        String jsonForHistoricWeather = "";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://meteostat.p.rapidapi.com/stations/monthly?station="
                    + stationId + "&start=2020-01-01&end=2020-12-31"))
                    .header("X-RapidAPI-Key", "3591847edamsh482247611f3042ap1ff135jsn1acaa240a433")
                    .header("X-RapidAPI-Host", "meteostat.p.rapidapi.com")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request,
                    HttpResponse.BodyHandlers.ofString());
            jsonForHistoricWeather = response.body();
            HistoricWeather result = GSON.fromJson(jsonForHistoricWeather, HistoricWeather.class);
            if (result.data[0].tavg != null) {
                double avgTemp = Double.parseDouble(result.data[0].tavg);
                String date = result.data[0].date;
                for (int i = 0; i < result.data.length; i++) {
                    if (result.data[i].tavg != null) {
                        double temp = Double.parseDouble(result.data[i].tavg);
                        if (Math.abs(temp - 24.0) < Math.abs(avgTemp - 24.0)) {
                            avgTemp = temp;
                            date = result.data[i].date;
                        }
                    }
                }
                String month = date.substring(5,7);
                double temp = avgTemp * 1.8 + 32;               
                if (month.equals("04")) {
                    String s = "Best time to go is April. Average temperature is " + temp + "°F";
                    Platform.runLater(() -> this.contentVBox.getChildren().add(new Text(s)));
                }
                if (month.equals("05")) {
                    String s = "Best time to go is May. Average temperature is " + temp + "°F";
                    Platform.runLater(() -> this.contentVBox.getChildren().add(new Text(s)));
                }
                if (month.equals("06")) {
                    String s = "Best time to go is June. Average temperature is " + temp + "°F";
                    Platform.runLater(() -> this.contentVBox.getChildren().add(new Text(s)));
                }
                if (month.equals("07")) {
                    String s = "Best time to go is July. Average temperature is " + temp + "°F";
                    Platform.runLater(() -> this.contentVBox.getChildren().add(new Text(s)));
                }
                if (month.equals("08")) {
                    String s = "Best time to go is August. Average temperature is " + temp + "°F";
                    Platform.runLater(() -> this.contentVBox.getChildren().add(new Text(s)));
                }
                if (month.equals("09")) {
                    String s = "Best time to go is September. Average temperature is " + temp
                        + "°F";
                    Platform.runLater(() -> this.contentVBox.getChildren().add(new Text(s)));
                }
                Platform.runLater(() -> this.getChildren().add(contentVBox));
            }
        } catch (Exception exc) {
            System.out.println("There was an exception!");
            System.out.println(exc);
        } 
    }
}
        

