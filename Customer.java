package TermProject.MovieReservationSystem;

public class Customer extends User {
    private int points; 

    public Customer(String id, String password, String name) {
        super(id, password, name);
        this.points = 0;
    }

    public void addPoints(int p) { this.points += p; }
    public int getPoints() { return points; }
    
    @Override
    public boolean isAdmin() { return false; }
}