package TermProject.MovieReservationSystem;

public class Admin extends User {
    public Admin(String id, String password, String name) {
        super(id, password, name);
    }

    @Override
    public boolean isAdmin() { return true; }
}