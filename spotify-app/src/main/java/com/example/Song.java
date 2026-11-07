package com.example;

public class Song {

    private String title;
    private String artist;
    private int year;
    private String genre;
    private String filePath; 
    private boolean isFavorite; 

    public String title() {
        return title;
    }

    public String artist() {
        return artist;
    }

    public int year() {
        return year;
    }

    public String genre() {
        return genre;
    } 

    public boolean isFavorite() { 
        return isFavorite;
    } 

    public void toggleFavorite() { 
        isFavorite = !isFavorite;
    }

    public String filePath() {
        return filePath;
    } 
}
