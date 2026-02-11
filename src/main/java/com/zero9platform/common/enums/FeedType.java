package com.zero9platform.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FeedType {
    // [%s] 형식 지정자 - 주차공간
    POPULAR("[%s] 상품이 현재 많은 관심을 받고 있습니다.", false),    // 찜 개인
    PAYMENT_COUNT("[%s] 상품에 대해 최근 %d명이 주문했습니다!", true), // 결제 전체
    PAYMENT_USER("[%s] 상품이 주문 완료되었습니다.", false),// 결제 개인
    DEADLINE("마감 안내: [%s] 상품의 판매가 곧 종료됩니다.", false),   // 마감 개인
    LOW_STOCK("[%s] 상품의 잔여 재고가 얼마 남지 않았습니다.", false),  // 재고 개인
    SOON("[%s] 상품외 여러 건이 곧 오픈 예정입니다.", false),    // 예정 전체,  요약
    SOLD_OUT("[%s] 상품이 판매 종료되었습니다.", false), // 품절 개인
    NOTICE("[공지] [%s]", false),     // 공지사항 (전체 공지) 전체
    EVENT("[이벤트] [%s]", false)       // 이벤트 (프로모션, 할인 등) 전체
    ;

    // 양식
    private final String messageFormat;

    // Redis에서 실시간 카운트(OrderCount 등)를 조회해야 하는 타입인지 여부
    private final boolean hasCounter;
    /**
     * 주차공간에 들어갈 실제 내용
     */
    public String toMessage(String targetName, long count) {
        try {
            // 실시간 카운트가 필요한 경우 (PAYMENT 등)
            if (this.hasCounter) {
                return String.format(this.messageFormat, targetName, count);
            }
            // 이름만 갈아끼우면 되는 경우
            return String.format(this.messageFormat, targetName);
        } catch (Exception e) {
            // 포맷팅 에러 시 원문 반환 (방어 로직)
            return this.messageFormat;
        }
    }
}