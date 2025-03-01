package org.example.pathfinder.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class TimeTrackingAPI {

    private static final String API_URL = "http://worldtimeapi.org/api/timezone/Etc/UTC";

    public static long getCurrentTime() {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.getLong("unixtime"); // Returns UNIX timestamp (seconds)

        } catch (Exception e) {
            System.out.println("⚠️ Failed to fetch time from API. Using system time instead.");
            return System.currentTimeMillis() / 1000; // 🔹 Use system time if API fails
        }
    }
}
