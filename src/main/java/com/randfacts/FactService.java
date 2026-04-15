package com.randfacts;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
				String content = "mock test " + category + " random shit";

				Fact newFact = new Fact(category + " fact", content, date);

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
