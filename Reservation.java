package TermProject.MovieReservationSystem;

import java.io.Serializable;

public class Reservation implements Serializable {
    private Customer customer;
    private Schedule schedule;
    private Seat seat;
    private int price;

    public Reservation(Customer customer, Schedule schedule, Seat seat, int price) {
        this.customer = customer;
        this.schedule = schedule;
        this.seat = seat;
        this.price = price;
    }

    public Customer getCustomer() { return customer; }
    public Schedule getSchedule() { return schedule; }
    public Seat getSeat() { return seat; }

    @Override
    public String toString() {
        return String.format("[예매완료] 영화: %s | 지점: %s(%s) | 좌석: %s | 결제금액: %d원",
                schedule.getMovie().getTitle(), schedule.getBranch(), schedule.getTimeZone(), seat.getSeatNumber(), price);
    }
}