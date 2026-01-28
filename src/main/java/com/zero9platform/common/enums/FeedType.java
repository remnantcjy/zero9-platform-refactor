package com.zero9platform.common.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FeedType {
    // [%s] 형식 지정자 - 주차공간
    POPULAR("[%s] 상품이 현재 많은 관심을 받고 있습니다."),    // 찜
    PAYMENT("[%s] 상품의 새로운 주문이 접수되었습니다."),    // 결제
    DEADLINE("마감 안내: [%s] 상품의 마감이 곧 종료됩니다."),   // 마감
    LOW_STOCK("[%s] 상품의 잔여 재고가 얼마 남지 않았습니다."),  // 재고
    SOON("[%s] 상품이 곧 공개될 예정입니다."),    // 예정
    SOLD_OUT("[%s] 상품이 재고 소진으로 인하여 판매 종료되었습니다."), // 품절
    NOTICE("[공지] [%s]"),     // 공지사항 (전체 공지)
    EVENT("[이벤트] [%s]")       // 이벤트 (프로모션, 할인 등)
    ;

    // 양식
    private final String messageFormat;

    /**
     * 주차공간에 들어갈 실제 내용
     */
    public String toMessage(String content) {
        return String.format(this.messageFormat, content);
    }
}
