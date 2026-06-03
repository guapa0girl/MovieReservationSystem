package TermProject.MovieReservationSystem;

import java.io.Serializable;

// 영화, 지점, 시간대, 상영관(좌석)을 하나로 묶는 스케줄 클래스
public class Schedule implements Serializable {
    private Movie movie;
    private String branch;   // 상영관 지점 (중계점, 노원점)
    private String timeZone; // 시간대 (오전, 오후)
    private Theater theater; // 독립적인 상영관(좌석) 객체

    public Schedule(Movie movie, String branch, String timeZone) {
        this.movie = movie;
        this.branch = branch;
        this.timeZone = timeZone;
        this.theater = new Theater(); 
    }

    public Movie getMovie() { return movie; }
    public String getBranch() { return branch; }
    public String getTimeZone() { return timeZone; }
    public Theater getTheater() { return theater; }
    
    @Override
    public String toString() {
        return String.format("[%s] 시간: %s | 상영작: %s", branch, timeZone, movie.toString());
    }
}