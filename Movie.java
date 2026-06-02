package TermProject.MovieReservationSystem;

import java.io.Serializable;

// [요구사항 반영: 영화 카테고리(장르) 분리]
public class Movie implements Serializable {
    private String title;
    private String genre; // 공포, 코미디 등

    public Movie(String title, String genre) {
        this.title = title;
        this.genre = genre;
    }

    public String getTitle() { return title; }
    public String getGenre() { return genre; }

    @Override
    public String toString() {
        return String.format("[%s] 장르: %s", title, genre);
    }
}