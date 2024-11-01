package fr.srbliveweather.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class APIService {
    // API key
    private static String apiKey = "abd0ebdc6db95f60dafa473cd6d7b979";

    /**
     * Call the OpenWeatherMap API
     * @param endpoint The endpoint of the API
     * @return The response from the API
     * @throws Exception If an error occurs
     */
    public static String callAPI(String endpoint) throws Exception {
        endpoint += "&appid=" + apiKey;

        URL url = new URL("https://api.openweathermap.org" + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        conn.disconnect();

        return content.toString();
    }

    /**
     * Get the current weather based on the latitude and longitude of a city
     *
     * @param lat The latitude of the city
     * @param lon The longitude of the city
     * @return The current weather of the city
     */
    public static JsonObject getWeather(double lat, double lon) {
        try {
            String apiResponse = callAPI(
                    "/data/2.5/weather?"
                            + "lat=" + lat
                            + "&lon=" + lon
            );

            // Parse the response
            JsonObject jsonObject = JsonParser.parseString(apiResponse).getAsJsonObject();

            // Extract the current weather from the JSON object
            //String weather = jsonObject.get("weather").getAsJsonArray().get(0).getAsJsonObject().get("main").getAsString();

            return jsonObject;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get the latitude and longitude of a city
     *
     * @param city The city
     * @return The latitude and longitude of the city
     */
    public static double[] getCoords(String city) {
        try {
            String apiResponse = callAPI(
                    "/geo/1.0/direct?"
                            + "q=" + city
                            + "&limit=1"
            );

            // Parse the response
            JsonArray jsonArray = JsonParser.parseString(apiResponse).getAsJsonArray();
            JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();

            // Extract lat and lon from the JSON object
            double lat = jsonObject.get("lat").getAsDouble();
            double lon = jsonObject.get("lon").getAsDouble();

            return new double[]{lat, lon};

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
