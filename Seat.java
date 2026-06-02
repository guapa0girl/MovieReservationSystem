package TermProject.MovieReservationSystem;

import java.io.Serializable;

public class Seat implements Serializable {
    private String seatNumber; 
    private boolean isReserved;
    private boolean isDisabledSeat;
    private boolean isPremiumSeat;

    public Seat(String seatNumber, boolean isDisabledSeat, boolean isPremiumSeat) {
        this.seatNumber = seatNumber;
        this.isDisabledSeat = isDisabledSeat;
        this.isPremiumSeat = isPremiumSeat;
        this.isReserved = false; 
    }

    public String getSeatNumber() { return seatNumber; }
    public boolean isReserved() { return isReserved; }
    public void setReserved(boolean reserved) { this.isReserved = reserved; }
    public boolean isDisabledSeat() { return isDisabledSeat; }
    public boolean isPremiumSeat() { return isPremiumSeat; }
}