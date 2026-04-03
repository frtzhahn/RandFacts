package com.randfacts;

/**
 * fact model representing a single random fact
 * handles the title, the content, and the generation date
 */
public class Fact {
    private String title;
    private String content;
    private String date;

    public Fact(String title, String content, String date) {
        this.title = title;
        this.content = content;
        this.date = date;
    }

    // getters and setters for data access and modification
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
