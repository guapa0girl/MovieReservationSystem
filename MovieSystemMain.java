package TermProject.MovieReservationSystem;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class MovieSystemMain {
    private static final String DATA_FILE = "cinema_final.dat";
    private static CinemaData data = new CinemaData();
    private static Scanner scanner = new Scanner(System.in);
    private static PricingPolicy pricingPolicy = new StandardPricing();
    private static User loggedInUser = null;

    public static void main(String[] args) {
        loadData();
        
        // 데이터가 비어있으면 초기 디폴트 데이터 세팅 (중계점/노원점 규칙)
        if (data.users.isEmpty()) {
            initDefaultData();
        }

        while (true) {
            if (loggedInUser == null) {
                showLoginMenu();
            } else if (loggedInUser.isAdmin()) {
                showAdminMenu(); // 관리자 메뉴
            } else {
                showCustomerMenu(); // 사용자 메뉴
            }
        }
    }

    // [요구사항 반영: 초기 데이터 자동 세팅]
    private static void initDefaultData() {
        System.out.println("시스템 초기 구동 중... 기본 데이터를 생성합니다.");
        data.users.add(new Admin("admin", "1234", "관리자"));
        
        Movie m1 = new Movie("군체", "공포");
        Movie m2 = new Movie("와일드씽", "코미디");
        data.movies.add(m1);
        data.movies.add(m2);

        // 중계점 규칙: 오전-군체, 오후-와일드씽
        data.schedules.add(new Schedule(m1, "중계점", "오전"));
        data.schedules.add(new Schedule(m2, "중계점", "오후"));
        
        // 노원점 규칙: 오전-와일드씽, 오후-군체
        data.schedules.add(new Schedule(m2, "노원점", "오전"));
        data.schedules.add(new Schedule(m1, "노원점", "오후"));
    }

    // ==========================================
    // 1. 공통 기능 (로그인 / 회원가입)
    // ==========================================
    private static void showLoginMenu() {
        System.out.println("\n========= 🎬 영화 예매 시스템 =========");
        System.out.println("1. 로그인  2. 회원가입  3. 종료");
        System.out.print("선택: ");
        int choice = scanner.nextInt(); scanner.nextLine();

        if (choice == 1) {
            System.out.print("아이디: "); String id = scanner.nextLine();
            System.out.print("비밀번호: "); String pw = scanner.nextLine();
            
            for (User u : data.users) {
                if (u.getId().equals(id) && u.checkPassword(pw)) {
                    loggedInUser = u;
                    System.out.println("\n[" + u.getName() + "]님 환영합니다.");
                    return;
                }
            }
            System.out.println("로그인 실패.");
        } 
        else if (choice == 2) {
            System.out.print("사용할 ID: "); String id = scanner.nextLine();
            for (User u : data.users) {
                if (u.getId().equals(id)) { System.out.println("중복된 ID입니다."); return; }
            }
            System.out.print("비밀번호: "); String pw = scanner.nextLine();
            System.out.print("이름: "); String name = scanner.nextLine();
            data.users.add(new Customer(id, pw, name));
            System.out.println("회원가입 완료!");
        } 
        else if (choice == 3) {
            saveData();
            System.exit(0);
        }
    }

    // ==========================================
    // 2. 일반 사용자 메뉴
    // ==========================================
    private static void showCustomerMenu() {
        System.out.println("\n========= [사용자 모드] =========");
        System.out.println("1. 영화 조회 및 예매");
        System.out.println("2. 내 예매 내역 확인");
        System.out.println("3. 예매 취소");
        System.out.println("4. 로그아웃");
        System.out.print("선택: ");
        int choice = scanner.nextInt(); scanner.nextLine();

        try {
            switch (choice) {
                case 1: reserveFlow(); break;
                case 2: viewReservations(); break;
                case 3: cancelReservation(); break;
                case 4: loggedInUser = null; break;
            }
        } catch (CinemaException e) {
            System.out.println("\n[오류] " + e.getMessage());
        }
    }

    // [핵심 요구사항 반영: 지점 선택 -> 카테고리 필터링 -> 예매]
    private static void reserveFlow() throws CinemaException {
        System.out.println("\n[1단계: 상영관 지점 선택]");
        System.out.println("1. 중계점   2. 노원점");
        System.out.print("선택: ");
        int branchChoice = scanner.nextInt(); scanner.nextLine();
        String selectedBranch = (branchChoice == 1) ? "중계점" : "노원점";

        System.out.println("\n[2단계: 조회 방식 선택]");
        System.out.println("1. 전체 영화 보기   2. 장르별(카테고리)로 나누어 보기");
        System.out.print("선택: ");
        int viewChoice = scanner.nextInt(); scanner.nextLine();
        
        String targetGenre = "";
        if (viewChoice == 2) {
            System.out.println("1. 공포 (군체)   2. 코미디 (와일드씽)");
            System.out.print("선택: ");
            int genreChoice = scanner.nextInt(); scanner.nextLine();
            targetGenre = (genreChoice == 1) ? "공포" : "코미디";
        }

        // 선택한 조건에 맞는 스케줄만 필터링하여 담을 임시 리스트
        ArrayList<Schedule> filteredSchedules = new ArrayList<>();
        
        System.out.println("\n[" + selectedBranch + " 상영 시간표]");
        for (Schedule sch : data.schedules) {
            if (sch.getBranch().equals(selectedBranch)) {
                // 장르별 보기를 선택했고, 장르가 다르면 건너뜀
                if (viewChoice == 2 && !sch.getMovie().getGenre().equals(targetGenre)) {
                    continue; 
                }
                filteredSchedules.add(sch);
            }
        }

        if (filteredSchedules.isEmpty()) {
            throw new CinemaException("조건에 맞는 상영 스케줄이 없습니다.");
        }

        // 필터링된 스케줄 출력
        for (int i = 0; i < filteredSchedules.size(); i++) {
            System.out.println((i + 1) + ". " + filteredSchedules.get(i));
        }

        System.out.print("\n예매할 스케줄 번호를 선택하세요: ");
        int schIndex = scanner.nextInt() - 1; scanner.nextLine();
        if (schIndex < 0 || schIndex >= filteredSchedules.size()) {
            throw new CinemaException("잘못된 번호입니다.");
        }
        
        Schedule selectedSchedule = filteredSchedules.get(schIndex);
        Theater theater = selectedSchedule.getTheater();

        System.out.print("관람 연령/대상을 입력하세요 (성인/청소년/어린이/경로/장애인): ");
        String ageGroup = scanner.nextLine();

        // 좌석 선택
        theater.printSeatMap(); 
        System.out.print("원하는 좌석의 행(A~E) 알파벳 입력: ");
        char row = scanner.nextLine().toUpperCase().charAt(0);
        System.out.print("원하는 좌석의 열(1~5) 숫자 입력: ");
        int col = scanner.nextInt(); scanner.nextLine();

        Seat seat = theater.reserveSeat(row, col);

        // A1(장애인석) 선택 시 검증 권고 메시지 출력
        if (seat.isDisabledSeat() && !ageGroup.equals("장애인")) {
            System.out.println("\n[안내] 선택하신 좌석은 장애인 전용석입니다. 현장에서 증빙 서류를 요구할 수 있습니다.");
        }

        // 요금 계산
        int finalPrice = pricingPolicy.calculatePrice(ageGroup, selectedSchedule.getTimeZone(), seat.isPremiumSeat());
        
        // 예매 내역 저장
        Customer me = (Customer) loggedInUser;
        Reservation res = new Reservation(me, selectedSchedule, seat, finalPrice);
        data.reservations.add(res);
        me.addPoints((int)(finalPrice * 0.05)); 

        System.out.println("\n🎉 예매 완료! 최종 결제 금액: " + finalPrice + "원");
    }

    private static void viewReservations() {
        System.out.println("\n[나의 예매 목록]");
        boolean found = false;
        for (Reservation r : data.reservations) {
            if (r.getCustomer().getId().equals(loggedInUser.getId())) {
                System.out.println(r);
                found = true;
            }
        }
        if (!found) System.out.println("예매 내역이 없습니다.");
    }

    private static void cancelReservation() throws CinemaException {
        System.out.println("\n[예매 취소]");
        int count = 1;
        for (Reservation r : data.reservations) {
            if (r.getCustomer().getId().equals(loggedInUser.getId())) {
                System.out.println(count++ + ". " + r);
            }
        }
        
        System.out.print("취소할 번호 선택 (취소 중단은 0): ");
        int cancelIdx = scanner.nextInt() - 1; scanner.nextLine();
        if (cancelIdx == -1) return;

        count = 0;
        for (int i = 0; i < data.reservations.size(); i++) {
            Reservation r = data.reservations.get(i);
            if (r.getCustomer().getId().equals(loggedInUser.getId())) {
                if (count == cancelIdx) {
                    r.getSchedule().getTheater().cancelSeat(r.getSeat().getSeatNumber());
                    data.reservations.remove(i);
                    System.out.println("예매가 취소되었습니다.");
                    return;
                }
                count++;
            }
        }
        throw new CinemaException("번호를 찾을 수 없습니다.");
    }

    // ==========================================
    // 3. 관리자 메뉴 (기본 제공)
    // ==========================================
    private static void showAdminMenu() {
        System.out.println("\n========= [관리자 모드] =========");
        System.out.println("1. 영화 등록/삭제 (추가 기능)");
        System.out.println("2. 로그아웃");
        System.out.print("선택: ");
        int choice = scanner.nextInt(); scanner.nextLine();

        if (choice == 1) {
            System.out.println("현재 등록된 영화는 초기 설정되어 변경할 수 없습니다 (군체, 와일드씽 고정)");
        } else if (choice == 2) {
            loggedInUser = null;
        }
    }

    // ==========================================
    // 4. 데이터 저장/로드 (파일 입출력)
    // ==========================================
    private static void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(data);
            System.out.println("데이터 저장 완료.");
        } catch (IOException e) {
            System.out.println("저장 오류: " + e.getMessage());
        }
    }

    private static void loadData() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                data = (CinemaData) ois.readObject();
            } catch (Exception e) {
                System.out.println("데이터 로드 실패. 초기화 진행.");
                data = new CinemaData();
            }
        }
    }
}