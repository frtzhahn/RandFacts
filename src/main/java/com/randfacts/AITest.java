package com.randfacts;

import io.github.cdimascio.dotenv.Dotenv;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;

class testGeminiResponse{
		List<testCandidate> candidates;

}

class testCandidate{
		testContent content;
}

class testContent{
		List<testPart> parts;
}

class testPart{
		String text;
}


public class AITest {
    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("GEMINI_API_KEY");

        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("api key not found");
            return;
        }

        System.out.println("Success: API Key loaded.");

        try {
            HttpClient client = HttpClient.newHttpClient();            
            String targetModel = "gemini-2.5-flash";
						URI uri = URI.create("https://generativelanguage.googleapis.com/v1beta/models/" + targetModel + ":generateContent?key=" + apiKey);

						String systemInstruction = """
						YOU ARE A FACT VERIFICATION ENGINE OPERATING UNDER STRICT EPISTEMIC CONSTRAINTS.
						YOUR SOLE FUNCTION IS TO PRODUCE ONE SINGLE FACT PER RESPONSE.

						ABSOLUTE OUTPUT RULES
						1. NO MARKDOWN. No asterisks, hashes, bold, italics, or backticks.
						2. NO BRACKET LABELS. Do not write [FACT], [SOURCE], [CONTEXT], [VERIFIED], or any similar tag.
						3. PLAIN TEXT ONLY. Four blocks of plain prose, separated by a single blank line each.
						4. DO NOT INCLUDE ANY PREAMBLE, INTRO, OR CLOSING STATEMENT.

						Your entire response is exactly four blocks. Nothing before block one. Nothing after block four.

						BLOCK STRUCTURE

						BLOCK 1 - THE FACT
						Start this block with the exact five characters: "Random Fact: " followed by a newline.
						Write the fact as a direct declarative sentence with no label or prefix.
						Use specific scientific names, precise numbers, exact locations, or official designations.
						No generalizations. No hedging words like "may," "might," "could," or "some researchers believe."

						BLOCK 2 - THE SOURCE
						Start this block with the exact five characters: "the source:" followed by a newline.
						On the next line, write the APA primary citation only.
						Format: Author, A. A. (Year). Title. Journal, Volume(Issue), Pages.
						Do not cite Wikipedia, trivia sites, or secondary summaries.

						BLOCK 3 - THE CONTEXT
						Start this block with the exact phrase: "The context for this fact is" and continue the sentence directly.
						State the boundary conditions, exceptions, and nuance that prevent misinterpretation.
						This block is mandatory. It must be substantive.

						BLOCK 4 - THE TIMESTAMP
						Write only: "Verified as of [Month Year]."
						If the fact is subject to change (records, measurements, population counts), append: " Subject to change."

						EPISTEMIC RULES
						5. IF YOU ARE NOT HIGHLY CONFIDENT IN THE PRIMARY SOURCE, OUTPUT THIS EXACT LINE AND NOTHING ELSE:
							INSUFFICIENT_CONFIDENCE: No verifiable primary source found. Fact withheld.
						6. DO NOT HALLUCINATE JOURNAL NAMES, VOLUME NUMBERS, ISSUE NUMBERS, OR PAGE RANGES.
						7. FACTS MUST BE FALSIFIABLE. No opinions, superlatives, or subjective claims.
						""";

						String userPrompt = "Give me a fascinating psychological fact about what women wants on a relationship.";

						Gson gson = new Gson();

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

            System.out.println("sending request to model: " + targetModel);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            System.out.println("response status code: " + response.statusCode());
						if(response.statusCode() == 200){
								testGeminiResponse geminiData = gson.fromJson(response.body(), testGeminiResponse.class);

								if(geminiData.candidates != null && !geminiData.candidates.isEmpty()){
										String extractedText = geminiData.candidates.get(0).content.parts.get(0).text;
										System.out.println("\u001b[31mEXTRACTED FACT\u001b[0m");
										System.out.println(extractedText);
								}
								else{
										System.err.println("no candiates found in response [raw body] " + response.body());	
								}

						}

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}



