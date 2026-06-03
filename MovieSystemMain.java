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
        
        // 데이터가 비어있으면 초기 디폴트 데이터 세팅
        if (data.users.isEmpty()) {
            initDefaultData();
        }

        // 메인 메뉴 루프
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

    // 초기 데이터 자동 세팅
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
        System.out.println("1. 로그인  2. 회원가입  3. 프로그램 종료");
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
            System.out.println("로그인 실패. 아이디나 비밀번호를 확인하세요.");
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
    // 2. 관리자 메뉴 (데이터 강제 초기화 포함)
    // ==========================================
    private static void showAdminMenu() {
        System.out.println("\n========= [관리자 모드] =========");
        System.out.println("1. 영화 등록/삭제 현황");
        System.out.println("2. 회원 관리 (포인트 조회)");
        System.out.println("3. 전체 데이터 강제 초기화 (RESET)"); 
        System.out.println("4. 로그아웃");
        System.out.print("선택: ");
        int choice = scanner.nextInt(); scanner.nextLine();

        if (choice == 1) {
            System.out.println("현재 등록된 영화는 초기 설정되어 변경할 수 없습니다 (군체, 와일드씽 고정)");
        } else if (choice == 2) {
            manageMembers(); // <--- 여기서 아래의 메서드를 호출합니다.
        } else if (choice == 3) {
            // 데이터 강제 초기화 로직
            System.out.print("경고: 모든 회원 및 예매 데이터가 삭제됩니다. 진행하시겠습니까? (Y/N): ");
            String confirm = scanner.nextLine();
            if (confirm.equalsIgnoreCase("Y")) {
                data = new CinemaData(); // 기존 데이터 덮어쓰기 (빈 깡통으로 만듦)
                initDefaultData();       // 기본 스케줄 재세팅
                saveData();              // 파일에 덮어쓰기
                System.out.println("시스템의 모든 데이터가 성공적으로 초기화되었습니다.");
                loggedInUser = null;     // 로그아웃
            } else {
                System.out.println("초기화를 취소했습니다.");
            }
        } else if (choice == 4) {
            loggedInUser = null;
        }
    }

    private static void manageMembers() {
        System.out.println("\n[가입된 일반 회원 목록 및 누적 포인트]");
        boolean hasCustomer = false;
        
        for (User u : data.users) {
            if (!u.isAdmin()) { 
                Customer c = (Customer) u;
                System.out.println("▶ ID: " + c.getId() + " | 이름: " + c.getName() + " | 누적 포인트: " + c.getPoints() + "P");
                hasCustomer = true;
            }
        }
        
        if (!hasCustomer) {
            System.out.println("현재 가입된 일반 회원이 없습니다.");
        }
        System.out.println("----------------------------------------");
    }

    // ==========================================
    // 3. 일반 사용자 메뉴
    // ==========================================
    private static void showCustomerMenu() {
        System.out.println("\n========= [사용자 모드] =========");
        System.out.println("1. 영화 조회 및 예매");
        System.out.println("2. 내 예매 내역 확인");
        System.out.println("3. 예매 취소 (포인트 회수)");
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

    // ★ 다인원 예매 로직이 적용된 메서드 ★
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

        ArrayList<Schedule> filteredSchedules = new ArrayList<>();
        System.out.println("\n[" + selectedBranch + " 상영 시간표]");
        for (Schedule sch : data.schedules) {
            if (sch.getBranch().equals(selectedBranch)) {
                if (viewChoice == 2 && !sch.getMovie().getGenre().equals(targetGenre)) {
                    continue; 
                }
                filteredSchedules.add(sch);
            }
        }

        if (filteredSchedules.isEmpty()) throw new CinemaException("조건에 맞는 상영 스케줄이 없습니다.");

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

        // -----------------------------------------------------------------
        // 다인원 예매 핵심 로직
        System.out.print("\n몇 명 예매하시겠습니까? (최대 5명): ");
        int ticketCount = scanner.nextInt(); scanner.nextLine();
        
        if (ticketCount < 1 || ticketCount > 5) {
            throw new CinemaException("예매 가능 인원은 1명 이상 5명 이하입니다.");
        }

        // 인원수만큼 연령대 배열 생성 및 입력 받기
        String[] ageGroups = new String[ticketCount];
        for (int i = 0; i < ticketCount; i++) {
            System.out.print((i + 1) + "번째 관람 연령/대상을 입력하세요 (성인/청소년/어린이/경로/장애인): ");
            ageGroups[i] = scanner.nextLine();
        }

        // 좌석 선택 루프 (인원수만큼 반복)
        Seat[] selectedSeats = new Seat[ticketCount];
        for (int i = 0; i < ticketCount; i++) {
            theater.printSeatMap(); 
            System.out.println("\n[" + (i + 1) + "번째 인원의 좌석 선택]");
            System.out.print("원하는 좌석의 행(A~E) 알파벳 입력: ");
            char row = scanner.nextLine().toUpperCase().charAt(0);
            System.out.print("원하는 좌석의 열(1~5) 숫자 입력: ");
            int col = scanner.nextInt(); scanner.nextLine();

            try {
                Seat seat = theater.reserveSeat(row, col);

                if (seat.isDisabledSeat() && !ageGroups[i].equals("장애인")) {
                    System.out.println("\n[안내] 선택하신 좌석은 장애인 전용석입니다. 현장에서 증빙 서류를 요구할 수 있습니다.");
                }
                selectedSeats[i] = seat; // 예매 성공 시 배열에 저장
            } catch (CinemaException e) {
                // 이미 예약된 좌석을 골랐거나 없는 좌석을 골랐을 때 예외 처리
                System.out.println("\n[오류] " + e.getMessage() + " 다시 선택해 주세요.");
                i--; // 반복 횟수를 되돌려 현재 인원의 좌석을 다시 고르도록 처리
            }
        }

        // 요금 합산 및 각 좌석별 예약 객체 생성
        int totalPrice = 0;
        Customer me = (Customer) loggedInUser;
        
        for (int i = 0; i < ticketCount; i++) {
            // 개별 인원 및 좌석 등급별 요금 계산
            int price = pricingPolicy.calculatePrice(ageGroups[i], selectedSchedule.getTimeZone(), selectedSeats[i].isPremiumSeat());
            totalPrice += price;
            
            // 시스템 상에는 각각의 예약으로 분리하여 저장 (부분 취소가 가능하도록)
            Reservation res = new Reservation(me, selectedSchedule, selectedSeats[i], price);
            data.reservations.add(res);
        }
        
        // 결제된 전체 금액에 대해 5% 포인트 통합 적립
        me.addPoints((int)(totalPrice * 0.05)); 

        System.out.println("\n🎉 총 " + ticketCount + "매 예매 완료! 최종 결제 금액: " + totalPrice + "원");
        // -----------------------------------------------------------------
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

    // 예매 취소 및 포인트 회수
    private static void cancelReservation() throws CinemaException {
        System.out.println("\n[예매 취소]");
        int count = 1;
        
        for (Reservation r : data.reservations) {
            if (r.getCustomer().getId().equals(loggedInUser.getId())) {
                System.out.println(count++ + ". " + r);
            }
        }
        
        if (count == 1) {
            System.out.println("현재 취소할 예매 내역이 없습니다.");
            return;
        }
        
        System.out.println("[안내] 다인원 예매의 경우, 내역에 나온 번호를 선택하여 개별(부분) 취소가 가능합니다.");
        System.out.print("취소할 번호 선택 (취소 중단은 0): ");
        int cancelIdx = scanner.nextInt() - 1; scanner.nextLine();
        if (cancelIdx == -1) return;

        count = 0;
        for (int i = 0; i < data.reservations.size(); i++) {
            Reservation r = data.reservations.get(i);
            if (r.getCustomer().getId().equals(loggedInUser.getId())) {
                if (count == cancelIdx) {
                    
                    // 1. 좌석 상태 원상복구
                    r.getSchedule().getTheater().cancelSeat(r.getSeat().getSeatNumber());
                    
                    // 2. 포인트 회수 (차감) 로직
                    int deductedPoints = (int)(r.getPrice() * 0.05); 
                    Customer me = (Customer) loggedInUser;
                    me.addPoints(-deductedPoints); 
                    
                    // 3. 내역 리스트에서 삭제
                    data.reservations.remove(i);
                    
                    System.out.println("\n✅ 예매가 성공적으로 취소되었습니다.");
                    System.out.println("[안내] 결제 취소에 따라 적립되었던 멤버십 포인트 [" + deductedPoints + "P]가 회수되었습니다.");
                    System.out.println("▶ 현재 잔여 포인트: " + me.getPoints() + "P");
                    return;
                }
                count++;
            }
        }
        throw new CinemaException("해당 예매 번호를 찾을 수 없습니다.");
    }

    // ==========================================
    // 4. 데이터 저장/로드 (파일 입출력)
    // ==========================================
    private static void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(data);
            System.out.println("데이터가 안전하게 저장되었습니다.");
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