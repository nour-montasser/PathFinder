package org.example.pathfinder.Service;  // ✅ Package should be at the top

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class DeepSeekClient {

    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";
    private static String API_KEY = System.getenv("DEEPSEEK_API_KEY");  // ✅ CORRECTED

    // Load API key from config.properties if not set as environment variable
    static {
        if (API_KEY == null || API_KEY.isEmpty()) {
            try {
                Properties properties = new Properties();
                properties.load(new FileInputStream("config.properties"));
                API_KEY = properties.getProperty("deepseek.api.key");
                if (API_KEY == null || API_KEY.isEmpty()) {
                    throw new IllegalStateException("⚠ API Key in config.properties is missing!");
                }
            } catch (Exception ignored) {
                System.out.println("Warning: API Key not found in config.properties.");
            }
        }
    }

    public static String getPriceEstimation(String serviceType) {
        if (API_KEY == null || API_KEY.isEmpty()) {
            throw new IllegalStateException("❌ API Key is missing. Set DEEPSEEK_API_KEY in environment variables or config.properties.");
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(API_URL);
            request.setHeader("Authorization", "Bearer " + API_KEY);
            request.setHeader("Content-Type", "application/json");

            // Construct JSON payload
            String jsonPayload = "{"
                    + "\"model\": \"deepseek-1.5\","
                    + "\"messages\": [{\"role\": \"system\", \"content\": \"You are an assistant that provides service price estimations.\"},"
                    + "{\"role\": \"user\", \"content\": \"What is the average price for " + serviceType + "?\"}],"
                    + "\"temperature\": 0.7"
                    + "}";

            request.setEntity(new StringEntity(jsonPayload, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(request);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8))) {

                StringBuilder responseBody = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBody.append(line);
                }

                // ✅ More Robust JSON Response Parsing
                JsonObject jsonResponse = JsonParser.parseString(responseBody.toString()).getAsJsonObject();

                if (jsonResponse.has("choices") && jsonResponse.getAsJsonArray("choices").size() > 0) {
                    JsonObject choice = jsonResponse.getAsJsonArray("choices").get(0).getAsJsonObject();
                    if (choice.has("message")) {
                        JsonObject message = choice.getAsJsonObject("message");
                        if (message.has("content")) {
                            return message.get("content").getAsString();
                        }
                    }
                }
                return "⚠ No valid response from DeepSeek API.";



            }
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error fetching price estimation: " + e.getMessage();
        }
    }


}
