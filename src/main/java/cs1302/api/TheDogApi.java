package cs1302.api;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileInputStream;
import java.util.Properties;

public class TheDogApi {

    public static class Dog {
        static Weight weight;
        // Height height;
        static String id;
        static String name;
        // String country_code;
        static String bred_for;
        static String breed_group;
        static String life_span;
        static String temperament;
        static String origin;
        // String reference_image_id;
        // Image image;
    }

    public static class Weight {
        String imperial;
        String metric;
    }

    public static class Height {
        String imperial;
        String metric;
    }

    public static class Image {
        String id;
        int width;
        int height;
        String url;
    }

    public static class DogResult {
        // int numFound;
        Dog dogs;
    }

    /** HTTP client. */
    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2) // uses HTTP protocol version 2 where possible
            .followRedirects(HttpClient.Redirect.NORMAL) // always redirects, except from HTTPS to HTTP
            .build();

    public static Gson GSON = new GsonBuilder()
            .setPrettyPrinting() // enable nice output when printing
            .create(); // builds and returns a Gson object

    private static String endPoint = "https://api.thedogapi.com/v1/breeds?apikey=";

    /**
     * Returns the response body string data from a URI.
     *
     * @param uri location of desired content
     * @return response body string
     * @throws IOException          if an I/O error occurs when sending or receiving
     * @throws InterruptedException if the HTTP client's {@code send} method is
     *                              interrupted
     */
    private static String fetchString(String uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();
        HttpResponse<String> response = HTTP_CLIENT
                .send(request, BodyHandlers.ofString());
        final int statusCode = response.statusCode();
        if (statusCode != 200) {
            throw new IOException("response status code not 200:" + statusCode);
        } // if
        System.out.println(response.body());
        return response.body().trim();
    } // fetchString

    /**
     * Return an {@code Optional} describing the root element of the JSON
     * response for a "search" query.
     *
     * @param q query string
     * @return an {@code Optional} describing the root element of the response
     */
    public static Optional<DogResult[]> search() {
        System.out.println("Searching for data");
        System.out.println("This may take some time to download...");
        String configPath = "resources/config.properties";
        // the following try-statement is called a try-with-resources statement
        // see
        // https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
        try (FileInputStream configFileStream = new FileInputStream(configPath)) {
            Properties config = new Properties();
            String apikey = config.getProperty("thedogapi.apikey");
            config.load(configFileStream);
            Dog.id = config.getProperty("theDog.id");
            TheDogApi.endPoint += apikey;
        } catch (IOException ioe) {
            System.err.println(ioe);
            ioe.printStackTrace();
        } // try
        try {
            String url = TheDogApi.endPoint;
            // URLEncoder.encode(q, StandardCharsets.UTF_8));
            String json = TheDogApi.fetchString(url);
            DogResult[] result = GSON.fromJson(json, DogResult[].class);
            // example1(GSON.fromJson(json, DogResult.class));
            return Optional.<DogResult[]>ofNullable(result);

        } catch (IllegalArgumentException | IOException | InterruptedException e) {
            return Optional.<DogResult[]>empty();
        } // try
    } // search

    /**
     * An example of some things you can do with a response.
     *
     * @param result the ope library search result
     */
    public static void example1(DogResult[] result) {
        // print what we found
        // for (DogResult doc : result) {
        // System.out.println(doc.dogs.name);
        // } // for
    } // example1
      // builds and returns a HttpClient object

    public static void main(String[] args) {
        // TheDogApi
        // .search()
        // .ifPresent(response -> example1(response));

        String configPath = "resources/config.properties";

        try (FileInputStream configFileStream = new FileInputStream(configPath)) {
            Properties config = new Properties();
            String apikey = config.getProperty("thedogapi.apikey");
            config.load(configFileStream);
            Dog.id = config.getProperty("theDog.id");
            Dog.name = config.getProperty("theDog.name");
            Dog.bred_for = config.getProperty("theDog.bred_for");
            Dog.breed_group = config.getProperty("theDog.breed_group");
            Dog.life_span = config.getProperty("theDog.life_span");
            Dog.temperament = config.getProperty("theDog.temperament");
            Dog.origin = config.getProperty("theDog.origin");
            Dog.weight.imperial = config.getProperty("theDog.weight.imperial");
            System.out.println(Dog.id);
            System.out.println(Dog.name);
            System.out.println(Dog.bred_for);
            System.out.println(Dog.breed_group);
            System.out.println(Dog.life_span);
            System.out.println(Dog.temperament);
            System.out.println(Dog.origin);
            // System.out.println(Dog.weight.imperial);
            // TheDogApi.endPoint += apikey;
        } catch (IOException ioe) {
            System.err.println(ioe);
            ioe.printStackTrace();
        } // try

    }
}
