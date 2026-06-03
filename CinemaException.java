package TermProject.MovieReservationSystem;

// 예외 처리 적용을 위한 사용자 정의 예외 클래스
public class CinemaException extends Exception {
    public CinemaException(String message) {
        super(message);
    }
}