package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.pathfinder.Service.DeepSeekClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PriceEstimationController {
    @FXML
    private TextField serviceInput; // Input field for service name
    @FXML
    private Button searchButton; // Search button
    @FXML
    private Label resultLabel; // Label to display the result
    private static final String API_KEY = "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"; // Replace with your actual API key
    //distilgpt2
    private static final String API_URL = "https://api-inference.huggingface.co/models/deepseek-ai/DeepSeek-R1-Distill-Qwen-32B"; // Model endpoint

    @FXML
    public void initialize() {
        searchButton.setOnAction(event -> fetchPriceEstimation());

        resultLabel.setText(generateNumber(buildPrompt("logo","TND")));    }

    private void fetchPriceEstimation() {
        String serviceName = serviceInput.getText().trim();
        String price;
        if (serviceName.isEmpty()) {
            resultLabel.setText("Please enter a service name.");
            return;
        }
        price = generateNumber(buildPrompt(serviceName,"TND"));
        System.out.println(price);
    }

    private String buildPrompt(String serviceName,String Unit) {
        return "Give me an average price of Freelance service  for "  +serviceName + "  just give me an estimation number in " + Unit +"and i  want the prompt to only be one number don't give me any details in 2025";
    }

    private String generateNumber(String prompt) {
        try {
            // Create the request body
            JSONObject requestData = new JSONObject();
            requestData.put("inputs", prompt); // Passing the complete prompt to the model

            // Send the request to Hugging Face API
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Write the request data
            try (OutputStream os = connection.getOutputStream()) {
                os.write(requestData.toString().getBytes());
                os.flush();
            }

            // Read the response
            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            // Parse the response
            JSONArray responseArray = new JSONArray(response.toString());
            String result = responseArray.getJSONObject(0).getString("generated_text").trim();

            // Remove the prompt from the result if it exists
            if (result.startsWith(prompt)) {
                result = result.substring(prompt.length()).trim();
            }

            // Extract the last number in the response
            String[] parts = result.split("\\D+"); // Split by non-digit characters
            String lastNumber = "";

            // Iterate through the parts to find the last number
            for (String part : parts) {
                if (!part.isEmpty()) {
                    lastNumber = part;
                }
            }

            // Return the last number or a default message if no number is found
            return lastNumber.isEmpty() ? "No valid price found." : lastNumber;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error generating price estimation."; // In case of error
        }
    }
}
