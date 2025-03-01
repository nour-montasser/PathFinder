package org.example.pathfinder.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class TextToSpeechAPI {

    private static final String API_KEY = "sk_d2d91b8b549fc6c6008c3a28c8493b6d4f1754053424f415";  // 🔹 Replace with your API Key
    private static final String VOICE_ID = "EXAVITQu4vr4xnSDxMaL";  // 🔹 Default ElevenLabs Voice

    public static void speak(String text) {
        try {
            String apiUrl = "https://api.elevenlabs.io/v1/text-to-speech/" + VOICE_ID;

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("xi-api-key", API_KEY);
            connection.setDoOutput(true);

            // 🔹 JSON Payload
            String jsonPayload = "{\"text\": \"" + text + "\", \"model_id\": \"eleven_monolingual_v1\", \"voice_settings\": {\"stability\": 0.5, \"similarity_boost\": 0.5}}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 🔹 Read API Response (MP3 Audio)
            InputStream inputStream = connection.getInputStream();
            File outputFile = new File("speech.mp3");

            try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
            }

            // 🔹 Play the audio
            playAudio("speech.mp3");

            inputStream.close();
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void playAudio(String filePath) {
        try {
            // Uses default system player to play the MP3 file
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                new ProcessBuilder("cmd", "/c", "start", filePath).start();
            } else {
                new ProcessBuilder("mpg123", filePath).start(); // Use mpg123 for Linux/Mac
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
