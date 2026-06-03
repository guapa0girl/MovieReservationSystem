package TermProject.MovieReservationSystem;

// 관리자 클래스
public class Admin extends User {
    public Admin(String id, String password, String name) {
        super(id, password, name);
    }

    @Override
    public boolean isAdmin() { return true; }
}