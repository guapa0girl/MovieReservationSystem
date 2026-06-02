package TermProject.MovieReservationSystem;

import java.io.Serializable;

// 파일 저장을 위해 Serializable 구현
public class StandardPricing implements PricingPolicy, Serializable {
    @Override
    public int calculatePrice(String ageGroup, String timeZone, boolean isPremiumSeat) {
        int basePrice = 14000; // 성인 기본 요금

        // 1. 대상별 요금 차등 적용 (어린이, 장애인 등 세분화)
        switch (ageGroup) {
            case "성인": basePrice = 14000; break;
            case "청소년": basePrice = 11000; break;
            case "어린이": basePrice = 8000; break;
            case "경로":
            case "장애인": basePrice = 7000; break;
            default: basePrice = 14000;
        }

        // 2. 시간대별 할인/할증 (조조/심야 등 추가 확장을 대비해 파라미터 유지)
        if (timeZone.equals("오전")) {
            // 오전 타임 일괄 할인 예시 (경로/장애인은 기존 가격 유지)
            basePrice = Math.min(basePrice, 10000); 
        }

        // 3. 프리미엄 좌석 할증
        if (isPremiumSeat) {
            basePrice += 2000;
        }

        return basePrice;
    }
}