package com.randfacts;

import io.github.cdimascio.dotenv.Dotenv;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AITest {
    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("GEMINI_API_KEY");

        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("CRITICAL ERROR: API Key not found!");
            return;
        }

        System.out.println("Success: API Key loaded.");

        try {
            HttpClient client = HttpClient.newHttpClient();
            
            // FIX 1: Add the missing "/" between "models" and the model name
            String targetModel = "gemini-2.5-flash";
						URI uri = URI.create("https://generativelanguage.googleapis.com/v1beta/models/" + targetModel + ":generateContent?key=" + apiKey);

            // FIX 2: We only declare jsonPayload once here. 
            // Removed the duplicate declaration outside the try block.
            String jsonPayload = """
            {
                "contents": [{
                    "parts":[{
                        "text": "Give me a fascinating psychological fact about human relationships."
                    }]
                }]
            }
            """;

            HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

            System.out.println("sending request to model: " + targetModel + "...");
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            System.out.println("response status code: " + response.statusCode());
            System.out.println("raw response: " + response.body());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
