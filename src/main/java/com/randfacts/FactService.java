package com.randfacts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import io.github.cdimascio.dotenv.Dotenv;

// data transfer object for gson
class GeminiResponse { List<Candidate> candidates; }
class Candidate { Content content; }
class Content { List<Part> parts; }
class Part { String text; }


public class FactService {
    private static FactService instance;
    private final HttpClient client;
    private final Gson gson;
    private final String apiKey;

    private List<Fact> history = new ArrayList<>();
    private List<Fact> savedFacts = new ArrayList<>();

    private FactService() {
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
        this.apiKey = Dotenv.load().get("GEMINI_API_KEY");
        
        // mock data for startup
        // history.add(new Fact("JVM: Java Virtual Machine", "The JVM is the engine that runs the Java bytecode", "2026-04-14"));
    }

    public static FactService getInstance() {
        if (instance == null) {
            instance = new FactService();
        }
        return instance;
    }

    public CompletableFuture<Fact> generateFactFromAI(String category) {
        String targetModel = "gemini-2.5-flash";
        // URI variable
        URI uri = URI.create("https://generativelanguage.googleapis.com/v1beta/models/" + targetModel + ":generateContent?key=" + apiKey);

        String systemInstruction = """
            YOU ARE A FACT VERIFICATION ENGINE OPERATING UNDER STRICT EPISTEMIC CONSTRAINTS.
            (Keep your amazing rules here...)
            """;

        // prompt dynamic based on the category parameter
        String userPrompt = "Give me a fascinating randomized psychological fact about " + category;

        //this.gson to avoid creating new one to save memory
        Map<String, Object> payload = Map.of(
            "system_instruction", Map.of(
                "parts", List.of(Map.of("text", systemInstruction))
            ),
            "contents", List.of(Map.of(
                "parts", List.of(Map.of("text", userPrompt))
            ))
        );

        String jsonPayload = gson.toJson(payload);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(uri)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
            .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(response -> {
                if (response.statusCode() != 200) {
                    throw new RuntimeException("AI API Error: " + response.body());
                }

                GeminiResponse geminiData = gson.fromJson(response.body(), GeminiResponse.class);
                String rawText = geminiData.candidates.get(0).content.parts.get(0).text;

                String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                Fact newFact = new Fact(category + " Fact", rawText, date);

                history.add(0, newFact);
                return newFact;
            });
    }

    // getters and helpers
    public List<Fact> getHistory() { return history; }
    public void saveFact(Fact fact) { if(!savedFacts.contains(fact)) savedFacts.add(0, fact); }
    public List<Fact> getSavedFacts() { return savedFacts; }
    public void updateSavedFact(Fact originalFact, String newContent) {
        originalFact.setContent(newContent);
    }
}

