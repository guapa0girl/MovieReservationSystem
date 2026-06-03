package TermProject.MovieReservationSystem;

// 일반 사용자 클래스
public class Customer extends User {
    private int points; // 멤버십 포인트

    public Customer(String id, String password, String name) {
        super(id, password, name);
        this.points = 0;
    }

    public void addPoints(int p) { this.points += p; }
    public int getPoints() { return points; }
    
    @Override
    public boolean isAdmin() { return false; }
}