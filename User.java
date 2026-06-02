package TermProject.MovieReservationSystem;

import java.io.Serializable;

public abstract class User implements Serializable {
    protected String id;       
    protected String password;
    protected String name;

    public User(String id, String password, String name) {
        this.id = id;
        this.password = password;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public boolean checkPassword(String pw) { return this.password.equals(pw); }
    
    // 다형성을 위한 추상 메서드
    public abstract boolean isAdmin(); 
}