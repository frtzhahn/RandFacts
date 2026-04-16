package com.randfacts;

// fact model representing a single random fact
// handles the title, the content, and the generation date

public class Fact {
    private String title;
    private String content;
    private String date;

		// prevents duped content
		private int id;

		// constructor for new AI facts
    public Fact(String title, String content, String date) {
        this.title = title;
        this.content = content;
        this.date = date;
    }

		// constructor for loading existing facts on db
		public Fact(String title, String content, String date, int id){
        this.title = title;
        this.content = content;
        this.date = date;
				this.id = id;

		}

    // getters and setters for data access and modification

		public int getId(){
				return id;
		}

		public void setId(int id){
				this.id = id;
		}

		public String getTitle(){
				return title;
		}

		public void setTitle(String title){
				this.title = title;
		}

		public String getContent(){
				return content;
		}

		public void setContent(String content){
				this.content = content;
		}

		public String getDate(){
				return date;
		}

		public void setDate(String date){
				this.date =date;
		}



}
