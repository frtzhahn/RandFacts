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
import java.sql.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;


// root response object for gemini api
class GeminiResponse { List<Candidate> candidates; }
// candidate selection block
class Candidate { Content content; }
// content container for response parts
class Content { List<Part> parts; }
// individual text part of the response
class Part { String text; }


public class FactService {
    // singleton instance for global access
    private static FactService instance;

    // http client for api communication
    private final HttpClient client;

    // gson for json processing
    private final Gson gson;

    // api key for gemini authentication
    private final String apiKey;

    // reference to the most recent generated fact
    private Fact latestFact;

    // database connection url file path
    private static final String DB_URL = "jdbc:sqlite:database/randfacts.db";

    // active sqlite connection
    private Connection conn;

    // in-memory session history
    private List<Fact> history = new ArrayList<>();

    // in-memory saved facts
    private List<Fact> savedFacts = new ArrayList<>();

    // constructor initializes core components and database connection
    private FactService() {
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
        this.apiKey = Dotenv.load().get("GEMINI_API_KEY");
        
        try {
            // creates database dir automatically if missing
            Files.createDirectories(Paths.get("database"));

            this.conn = DriverManager.getConnection(DB_URL);
            initializeDatabase(); 
            loadHistoryFromDB();
        }
        catch(SQLException | IOException e) {
            System.err.println("database connection failed" + e.getMessage());
        }
    }

    // creates facts table if missing from database
    private void initializeDatabase() throws SQLException {
        String createTableSQL = """
                CREATE TABLE IF NOT EXISTS facts(
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            title TEXT NOT NULL,
                            content TEXT NOT NULL, 
                            date TEXT NOT NULL, 
                            is_saved INTEGER DEFAULT 0
                        );
        """;

        try(Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    // retrieves all stored facts from sqlite into memory lists
    private void loadHistoryFromDB() {
        String sql = "SELECT * FROM facts ORDER BY id DESC";

        try(Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            
            while(rs.next()) {
                Fact fact = new Fact(
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("date"),
                        rs.getInt("id")
                        );
                history.add(fact);

                // separate saved facts into dedicated list
                if(rs.getInt("is_saved") == 1) {
                    savedFacts.add(fact);
                }
            }
            System.out.println("\u001b[32mdatabase: facts stored " + history.size() + " facts from your disk\u001b[0m");
        }
        catch(SQLException e) {
            System.err.println("\u001b31mdatabase error: failed to load history " + e.getMessage());
        }
    }

    // inserts new generated fact into the database and assigns generated id
    private void persistentFactToDB(Fact fact, boolean isSaved) {
        String sql = "INSERT INTO facts (title, content, date, is_saved) VALUES (?, ?, ?, ?)";

        try(PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, fact.getTitle());
            pstmt.setString(2, fact.getContent());
            pstmt.setString(3, fact.getDate());
            pstmt.setInt(4, isSaved ? 1 : 0);
            pstmt.executeUpdate();

            // retrieve and set the auto-generated id
            ResultSet rs = pstmt.getGeneratedKeys();
            if(rs.next()) {
                int newId = rs.getInt(1);
                fact.setId(newId);
                System.out.println("\u001b[32mdatabase: saved to your local device commited fact #" + newId + "\u001b[0m");				
            }
        }
        catch(SQLException e) {
            System.err.println("\u001b[31mdatabase error \u001b[0m" + e.getMessage());
        }
    }

    // returns singleton instance of the service
    public static FactService getInstance() {
        if (instance == null) {
            instance = new FactService();
        }
        return instance;
    }

    // entry point for ai fact generation chain starting with primary model
    public CompletableFuture<Fact> generateFactFromAI(String category) {
        String[] modelChain = {"gemini-2.5-flash", "gemini-2.5-flash-lite"};
        return tryModelChain(category, modelChain, 0);
    }

    // handles sequential fallback logic and network error recovery between models
    private CompletableFuture<Fact> tryModelChain(String category, String[] models, int index) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // fallback to busy message if all gemini models fail hoping it won't lol
        if (index >= models.length) {
            System.err.println("\u001b[31mGemini AI models exhausted or ran out of api calls\u001b[0m");
            Fact busyFact = new Fact("Gemini Status", "Gemini is too busy at the moment please try again later", date);
            return CompletableFuture.completedFuture(busyFact);
        }

        String targetModel = models[index];
        URI uri = URI.create("https://generativelanguage.googleapis.com/v1beta/models/" + targetModel + ":generateContent?key=" + apiKey);

        Map<String, Object> payload = createPayload(category);
        String jsonPayload = gson.toJson(payload);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(uri)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
            .build();

        System.out.println("\u001b[32mfact generation with: " + targetModel + "\u001b[0m");

        // handle response or exceptions asynchronously
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .handle((response, ex) -> {
                if (ex != null || (response != null && response.statusCode() != 200)) {
                    if (ex != null) {
                        System.err.println("\u001b[31m[RESILIENCE] Network error on " + targetModel + ": " + ex.getMessage() + "\u001b[0m");
                    } else if (response != null) {
                        System.err.println("\u001b[33m[RESILIENCE] " + targetModel + " failed (Status: " + response.statusCode() + "). Triggering Fallback...\u001b[0m");
                    }
                    return tryModelChain(category, models, index + 1);
                }
                return CompletableFuture.completedFuture(processSuccessfulResponse(response.body(), category, date));
            })
            .thenCompose(fut -> fut);
    }

    // constructs json payload with strict guardrails for ai output
    private Map<String, Object> createPayload(String category) {
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
                Start this block with the exact five characters: "Random Fact: ".
                Write the fact as a direct declarative sentence with no label or prefix.
                Use specific scientific names, precise numbers, exact locations, or official designation.
                No generalizations. No hedging words like "may," "might," "could," or "some researchers believe."

                BLOCK 2 - THE SOURCE
                Start this block with the exact five characters: "SOURCE:" followed by a newline.
                On the next line, write the APA primary citation only.
                Format: Author, A. A. (Year). Title. Journal, Volume(Issue), Pages.
                Do not cite Wikipedia, trivia sites, or secondary summaries.

                BLOCK 3 - THE CONTEXT
                Start this block with the exact phrase: "This topic explores" and continue the sentence directly.
                State the boundary conditions, exceptions, and nuance that prevent misinterpretation.
                This block is mandatory. It must be substantive.

                BLOCK 4 - THE TIMESTAMP
                Write only: "This fact is verified as of [Month Year]."
                If the fact is subject to change (records, measurements, population counts), append: " Subject to change."

                EPISTEMIC RULES
                5. IF YOU ARE NOT HIGHLY CONFIDENT IN THE PRIMARY SOURCE, OUTPUT THIS EXACT LINE AND NOTHING ELSE:
                    INSUFFICIENT_CONFIDENCE: No verifiable primary source found. Fact withheld.
                6. DO NOT HALLUCINATE JOURNAL NAMES, VOLUME NUMBERS, ISSUE NUMBERS, OR PAGE RANGES.
                7. FACTS MUST BE FALSIFIABLE. No opinions, superlatives, or subjective claims.
                """;

				// main prompt that contains the task for gemini
        String userPrompt = "Give me a fascinating randomized fact about " + category;

        return Map.of(
            "system_instruction", Map.of(
                "parts", List.of(Map.of("text", systemInstruction))
            ),
            "contents", List.of(Map.of(
                "parts", List.of(Map.of("text", userPrompt))
            ))
        );
    }

    // parses successful ai response, saves to database, and updates session history
    private Fact processSuccessfulResponse(String responseBody, String category, String date) {
        GeminiResponse geminiData = gson.fromJson(responseBody, GeminiResponse.class);
        String rawText = geminiData.candidates.get(0).content.parts.get(0).text;

        Fact newFact = new Fact(category + " Fact", rawText, date);
        persistentFactToDB(newFact, false);

        history.add(0, newFact);
        this.latestFact = newFact;
        return newFact;
    }

    // returns list of generated facts for current session
    public List<Fact> getHistory() { 
        return history; 
    }

    // marks fact as saved in memory and triggers database update
    public void saveFact(Fact fact) { 
        if(!savedFacts.contains(fact)) {
            savedFacts.add(0, fact);
            updateSavedStatusInDB(fact.getId(), true);
        }
    }

    // updates is_saved status for specific record in sqlite database
    private void updateSavedStatusInDB(int id, boolean isSaved) {
        String sql = "UPDATE facts SET is_saved = ? WHERE id = ?";

        try(PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, isSaved ? 1 : 0);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            
            System.out.println("\u001b[32mDatabase fact #" + id + " permanently saved\u001b[0m");
        }
        catch(SQLException e) {
            System.err.println("\u001b[31mdatabase update failed" + e.getMessage());
        }
    }

    // returns list of all facts marked as saved by me and the potential user
    public List<Fact> getSavedFacts() { 
        return savedFacts; 
    }

    // updates the content string of an existing fact object
    public void updateSavedFact(Fact originalFact, String newContent) {
        originalFact.setContent(newContent);
    }

    // permanently removes fact from memory and database
    public void deleteFactPermanently(Fact fact) {
        if (fact == null) return;

        // remove from in-memory history and saved lists
        history.remove(fact);
        savedFacts.remove(fact);

        // clear latest fact reference if it matches the deleted fact
        if (latestFact != null && latestFact.equals(fact)) {
            latestFact = null;
        }

        String sql = "DELETE FROM facts WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, fact.getId());
            pstmt.executeUpdate();
            System.out.println("\u001b[32mfact #" + fact.getId() + " permanently deleted\u001b[0m");
        } catch (SQLException e) {
            System.err.println("\u001bdeletion failed\u001b[0m " + e.getMessage());
        }
    }

    // returns most recently generated fact object
    public Fact getLatestFact() {
        return latestFact;
    }

    // forms statistics based on data from the pages selected
    public Map<String, Integer> getCategoryStats(String mode) {
        Map<String, Integer> stats = new java.util.LinkedHashMap<>();
        
        // determine query based on contextual slice
        String sql = switch(mode) {
            case "SAVED FACTS" -> "SELECT title, COUNT(*) as count FROM facts WHERE is_saved = 1 GROUP BY title ORDER BY count DESC";
            case "HISTORY" -> "SELECT title, COUNT(*) as count FROM facts GROUP BY title ORDER BY count DESC";
            default -> "SELECT title, COUNT(*) as count FROM facts GROUP BY title ORDER BY count DESC"; 
        };

        try(Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            while(rs.next()) {
                stats.put(rs.getString("title"), rs.getInt("count"));
            }
        }
        catch(SQLException e) {
            System.err.println("\u001b[31mDatabase error: stats query failed\u001b[0m" + e.getMessage());
        }
        return stats;
    }
}
