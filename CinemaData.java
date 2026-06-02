package TermProject.MovieReservationSystem;

import java.io.Serializable;
import java.util.ArrayList;

public class CinemaData implements Serializable {
    public ArrayList<User> users = new ArrayList<>();
    public ArrayList<Movie> movies = new ArrayList<>();
    public ArrayList<Schedule> schedules = new ArrayList<>();
    public ArrayList<Reservation> reservations = new ArrayList<>();
}