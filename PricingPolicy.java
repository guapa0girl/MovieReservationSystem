package TermProject.MovieReservationSystem;

public interface PricingPolicy {
    int calculatePrice(String ageGroup, String timeZone, boolean isPremiumSeat);
}