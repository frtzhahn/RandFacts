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
		private Fact latestFact;

		private static final String DB_URL = "jdbc:sqlite:database/randfacts.db";
		private Connection conn;

    private List<Fact> history = new ArrayList<>();
    private List<Fact> savedFacts = new ArrayList<>();

    private FactService() {
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
        this.apiKey = Dotenv.load().get("GEMINI_API_KEY");
        
        // mock data for startup
        // history.add(new Fact("JVM: Java Virtual Machine", "The JVM is the engine that runs the Java bytecode", "2026-04-14"));

				try{
						this.conn = DriverManager.getConnection(DB_URL);
						initializeDatabase(); 
						loadHistoryFromDB();
				}
				catch(SQLException e){
						System.err.println("database connection failed" + e.getMessage());
				}
    }

		private void initializeDatabase() throws SQLException{
				String createTableSQL = """
						CREATE TABLE IF NOT EXISTS facts(
									id INTEGER PRIMARY KEY AUTOINCREMENT,
									title TEXT NOT NULL,
									content TEXT NOT NULL, 
									date TEXT NOT NULL, 
									is_saved INTEGER DEFAULT 0
								);
				""";

				try(Statement stmt = conn.createStatement()){
						stmt.execute(createTableSQL);
				}
		}

		private void loadHistoryFromDB(){
				String sql = "SELECT * FROM facts ORDER BY id DESC";

				try(Statement stmt = conn.createStatement();
						ResultSet rs = stmt.executeQuery(sql)){
					
					while(rs.next()){
							Fact fact = new Fact(
									rs.getString("title"),
									rs.getString("content"),
									rs.getString("date"),
									rs.getInt("id")
									);
							history.add(fact);

							if(rs.getInt("is_saved") == 1){
									savedFacts.add(fact);
							}
					}
					System.out.println("\u001b[32mDatabase: facts stored " + history.size() + " facts from your disk\u001b[0m");
				}
				catch(SQLException e){
						System.err.println("\u001b31mDatabase error: failed to load history " + e.getMessage());
				}
		}

		private void persistentFactToDB(Fact fact, boolean isSaved){

				String sql = "INSERT INTO facts (title, content, date, is_saved) VALUES (?, ?, ?, ?)";

				try(PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
						pstmt.setString(1, fact.getTitle());
						pstmt.setString(2, fact.getContent());
						pstmt.setString(3, fact.getDate());
						pstmt.setInt(4, isSaved ? 1 : 0);
						pstmt.executeUpdate();

						ResultSet rs = pstmt.getGeneratedKeys();
						if(rs.next()){
								int newId = rs.getInt(1);
								fact.setId(newId);
								System.out.println("\u001b[32mDatabase: saved to local device commited fact #" + newId + "\u001b[0m");				
								
						}

				}
				catch(SQLException e){
						System.err.println("\u001b[31mDatabase error \u001b[0m" + e.getMessage());
				}
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

        // prompt dynamic based on the category parameter
        String userPrompt = "Give me a fascinating randomized fact about " + category;

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
								persistentFactToDB(newFact, false);

                history.add(0, newFact);
								this.latestFact = newFact;
                return newFact;
            });
    }

    // getters and helpers
    public List<Fact> getHistory(){ 
				return history; 
		}

    public void saveFact(Fact fact){ 
				if(!savedFacts.contains(fact)){
						savedFacts.add(0, fact);
						updateSavedStatusInDB(fact.getId(), true);
				}
		}

		private void updateSavedStatusInDB(int id, boolean isSaved){
				String sql = "UPDATE facts SET is_saved = ? WHERE id = ?";

				try(PreparedStatement pstmt = conn.prepareStatement(sql)){
						pstmt.setInt(1, isSaved ? 1 : 0);
						pstmt.setInt(2, id);
						pstmt.executeUpdate();
						
						System.out.println("\u001b[32mDatabase fact #" + id + " permanently saved\u001b[0m");
				}
				catch(SQLException e){
						System.err.println("\u001b[31mdatabase update failed" + e.getMessage());
				}
		}

    public List<Fact> getSavedFacts(){ 
				return savedFacts; 
		}

    public void updateSavedFact(Fact originalFact, String newContent) {
        originalFact.setContent(newContent);
    }

		public Fact getLatestFact(){
				return latestFact;
		}

		public Map<String, Integer> getCategoryStats(){
				Map<String, Integer> stats = new java.util.LinkedHashMap<>();

				String sql = """
						SELECT title, COUNT(*) as count
						FROM facts
						GROUP BY title
						ORDER BY count DESC;
				""";

				try(Statement stmt = conn.createStatement();
						ResultSet rs = stmt.executeQuery(sql)){

					while(rs.next()){
							stats.put(rs.getString("title"), rs.getInt("count"));
					}
				}
				catch(SQLException e){
						System.err.println("\u001b[31mDatabase error: sstats query failed\u001b[0m" + e.getMessage());
				}
				return stats;
		}
}

