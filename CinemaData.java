package TermProject.MovieReservationSystem;

import java.io.Serializable;
import java.util.ArrayList;

// 파일 저장을 위한 데이터베이스 역할의 래퍼(Wrapper) 클래스
public class CinemaData implements Serializable {
    public ArrayList<User> users = new ArrayList<>();
    public ArrayList<Movie> movies = new ArrayList<>();
    public ArrayList<Schedule> schedules = new ArrayList<>();
    public ArrayList<Reservation> reservations = new ArrayList<>();
}