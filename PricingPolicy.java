package TermProject.MovieReservationSystem;

// 요금 계산을 위한 인터페이스 (다형성 활용)
public interface PricingPolicy {
    int calculatePrice(String ageGroup, String timeZone, boolean isPremiumSeat);
}