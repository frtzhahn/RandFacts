package com.randfacts;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
// import io.github.cdimascio.dotenv.Dotenv;

public class FactService{
		private static FactService instance;

		private List<Fact> history = new ArrayList<>();
		private List<Fact> savedFacts = new ArrayList<>();

		private FactService(){
				history.add(new Fact("JVM: Java Virtual Machine", "The JVM is the engine that runs the Java bytecode", "2026-04-14"));

		}

		public static FactService getInstance(){
				if(instance == null){
						instance = new FactService();
				}
				return instance;
		}

		public Fact generateFact(String category){
				String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				String title = category + " random facts";
				String content;
				String normalizedCategory = category.toLowerCase();

				switch(normalizedCategory){
					case "programming":
						content = "The first programmer in history was Ada Lovelace, who wrote an algorithm for Charles Babbage's Analytical Engine in the  mid-1800s.";
						break;

				  case "History":
            content = "The Ancient Romans used to wash their clothes in urine because the ammonia it contains acted as a powerful cleaning agent.";
            break;

          case "Science":
            content = "A single bolt of lightning contains enough energy to toast 100,000 slices of bread.";
            break;

          case "Astronomy":
            content = "If two pieces of the same type of metal touch in space, they will permanently bond together. This is called 'Cold Welding'.";
            break;

          case "Philosophy":
            content = "The 'Ship of Theseus' is a thought experiment that asks whether an object that has had all of its components replaced remains fundamentally the same object.";
            break;

					default:
						content = "Every time you learn something new, your brain physically changes its structure. Keep exploring the " + category + " category!";
						break;
				}

				Fact newFact = new Fact(title, content, date);
				history.add(0, newFact);
				return newFact;
		}

		public List<Fact> getHistory(){
				return history;
		}

		public void saveFact(Fact fact){
				if(!savedFacts.contains(fact)){
						savedFacts.add(0, fact);
				}
		}

		public List<Fact> getSavedFacts(){
				return savedFacts;
		}

	 	public void updateSavedFact(Fact originalFact, String newContent) {
		 		originalFact.setContent(newContent);
			 	System.out.println("Service: data model synchronized with UI edits.");
	 	}

}
