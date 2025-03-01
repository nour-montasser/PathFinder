package org.example.pathfinder.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class DictionaryAPI {

    private static final String API_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/";

    public static String getDefinition(String word) {
        try {
            // 🔹 Build API URL
            URL url = new URL(API_URL + word);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // 🔹 Read API response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // 🔹 Parse JSON response
            JSONArray jsonArray = new JSONArray(response.toString());
            JSONObject firstEntry = jsonArray.getJSONObject(0);
            JSONArray meanings = firstEntry.getJSONArray("meanings");
            JSONObject firstMeaning = meanings.getJSONObject(0);
            JSONArray definitions = firstMeaning.getJSONArray("definitions");
            JSONObject firstDefinition = definitions.getJSONObject(0);

            // 🔹 Extract definition and synonyms (if available)
            String definition = firstDefinition.getString("definition");
            String result = "**Definition:** " + definition;

            if (firstDefinition.has("synonyms")) {
                JSONArray synonymsArray = firstDefinition.getJSONArray("synonyms");
                if (synonymsArray.length() > 0) {
                    result += "\n**Synonyms:** " + synonymsArray.join(", ").replace("\"", "");
                }
            }

            return result;

        } catch (Exception e) {
            return "⚠️ No definition found for \"" + word + "\".";
        }
    }
}
