package TermProject.MovieReservationSystem;

import java.io.Serializable;

// 상영관 좌석 배열 (5x5) 관리 클래스
public class Theater implements Serializable {
    private Seat[][] seats;
    private final int ROWS = 5; // A~E행
    private final int COLS = 5; // 1~5열

    public Theater() {
        seats = new Seat[ROWS][COLS];
        initializeSeats();
    }

    private void initializeSeats() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                String seatNum = (char)('A' + i) + String.valueOf(j + 1);
                
                // 맨 앞 좌측 (0, 0)을 무조건 장애인석으로 설정
                if (i == 0 && j == 0) {
                    seats[i][j] = new Seat(seatNum, true, false);
                } 
                // 맨 뒷줄 E행은 프리미엄석으로 설정
                else if (i == ROWS - 1) {
                    seats[i][j] = new Seat(seatNum, false, true);
                } 
                else {
                    seats[i][j] = new Seat(seatNum, false, false);
                }
            }
        }
    }

    // 좌석 예매 (오류 시 예외 발생)
    public Seat reserveSeat(char rowChar, int colNum) throws CinemaException {
        int row = rowChar - 'A';
        int col = colNum - 1;

        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
            throw new CinemaException("상영관 범위를 벗어난 좌석 번호입니다.");
        }
        if (seats[row][col].isReserved()) {
            throw new CinemaException("이미 예매된 좌석입니다.");
        }
        seats[row][col].setReserved(true);
        return seats[row][col];
    }
    
    // 예매 취소 시 좌석 원상복구
    public void cancelSeat(String seatNumber) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (seats[i][j].getSeatNumber().equals(seatNumber)) {
                    seats[i][j].setReserved(false); 
                }
            }
        }
    }

    // 좌석표 출력
    public void printSeatMap() {
        System.out.println("\n        [ SCREEN ]");
        System.out.println("   1    2    3    4    5");
        for (int i = 0; i < ROWS; i++) {
            System.out.print((char)('A' + i) + " ");
            for (int j = 0; j < COLS; j++) {
                if (seats[i][j].isReserved()) System.out.print("[ X ]");
                else if (seats[i][j].isDisabledSeat()) System.out.print("[ W ]"); // 장애인석
                else if (seats[i][j].isPremiumSeat()) System.out.print("[ P ]"); // 프리미엄석
                else System.out.print("[ O ]");
            }
            System.out.println();
        }
        System.out.println("O: 일반, X: 예매됨, W: 장애인석(A1고정), P: 프리미엄석\n");
    }
}