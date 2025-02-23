package org.example.pathfinder.Service;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.example.pathfinder.Model.Question;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AISkillTestService {
    private static final String API_KEY = ".";  // Replace with a valid API Key
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + API_KEY;

    public static List<Question> generateSkillTest(String topic) {
        OkHttpClient client = new OkHttpClient();

        // ✅ Construct AI Prompt
        String prompt = "Generate a multiple-choice skill test on " + topic + " with 5 questions. " +
                "Each question should have 4 answer choices labeled (a, b, c, d), " +
                "with the correct answer indicated at the end. " +
                "Format it as follows:\n\n" +
                "**Question X:** <question text>\n" +
                "a) <option 1>\n" +
                "b) <option 2>\n" +
                "c) <option 3>\n" +
                "d) <option 4>\n" +
                "**Correct Answer:** <correct option letter>";

        // ✅ Correctly Format JSON Request for Gemini API
        JSONObject requestBody = new JSONObject();
        JSONArray contentsArray = new JSONArray();
        JSONObject contentObject = new JSONObject();
        JSONArray partsArray = new JSONArray();
        JSONObject textObject = new JSONObject();

        textObject.put("text", prompt);
        partsArray.put(textObject);
        contentObject.put("parts", partsArray);
        contentsArray.put(contentObject);
        requestBody.put("contents", contentsArray);

        RequestBody body = RequestBody.create(requestBody.toString(), MediaType.get("application/json"));
        Request request = new Request.Builder().url(API_URL).post(body).build();

        try {
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();

            // ✅ Debugging Response
            System.out.println("🔹 Full API Response: " + responseBody);

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected API Response: " + responseBody);
            }

            // ✅ Parse JSON Response
            JSONObject jsonResponse = new JSONObject(responseBody);
            if (!jsonResponse.has("candidates")) {
                System.err.println("❌ Error: Missing 'candidates' key in response.");
                return new ArrayList<>();
            }

            JSONArray candidates = jsonResponse.getJSONArray("candidates");
            if (candidates.isEmpty()) {
                System.err.println("❌ Error: Empty 'candidates' array.");
                return new ArrayList<>();
            }

            // ✅ Extract AI-generated text
            JSONObject firstCandidate = candidates.getJSONObject(0);
            if (!firstCandidate.has("content")) {
                System.err.println("❌ Error: Missing 'content' key.");
                return new ArrayList<>();
            }

            // FIXED: `"content"` is a JSONObject, not a JSONArray
            JSONObject contentObjectResponse = firstCandidate.getJSONObject("content");
            JSONArray partsArrayResponse = contentObjectResponse.getJSONArray("parts");

            if (partsArrayResponse.isEmpty()) {
                System.err.println("❌ Error: Empty 'parts' array.");
                return new ArrayList<>();
            }

            String outputText = partsArrayResponse.getJSONObject(0).getString("text");
            return parseAIResponse(outputText);

        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ✅ Parses AI-generated text into Question objects
    private static List<Question> parseAIResponse(String responseText) {
        List<Question> questions = new ArrayList<>();

        // ✅ Regex patterns to extract questions, choices, and correct answers
        Pattern questionPattern = Pattern.compile("\\*\\*Question \\d+:\\*\\* (.*?)\\n");
        Pattern choicesPattern = Pattern.compile("\\n(a)\\) (.*?)\\n(b)\\) (.*?)\\n(c)\\) (.*?)\\n(d)\\) (.*?)\\n");
        Pattern answerPattern = Pattern.compile("\\*\\*Correct Answer:\\*\\* (\\w)");

        Matcher questionMatcher = questionPattern.matcher(responseText);
        Matcher choicesMatcher = choicesPattern.matcher(responseText);
        Matcher answerMatcher = answerPattern.matcher(responseText);

        while (questionMatcher.find() && choicesMatcher.find() && answerMatcher.find()) {
            String questionText = questionMatcher.group(1);
            String optionA = choicesMatcher.group(2);
            String optionB = choicesMatcher.group(4);
            String optionC = choicesMatcher.group(6);
            String optionD = choicesMatcher.group(8);
            String correctAnswer = answerMatcher.group(1);

            String correctResponse = switch (correctAnswer.toLowerCase()) {
                case "a" -> optionA;
                case "b" -> optionB;
                case "c" -> optionC;
                case "d" -> optionD;
                default -> "Unknown";
            };

            // ✅ Create Question object and add it to the list
            Question q = new Question(null, questionText, null, optionA + "," + optionB + "," + optionC + "," + optionD, correctResponse, 10);
            questions.add(q);
        }

        return questions;
    }
}
