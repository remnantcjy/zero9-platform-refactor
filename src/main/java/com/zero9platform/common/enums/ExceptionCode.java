package com.zero9platform.common.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {

    NOT_FOUND_POST(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),

    //검색
    NOT_FOUND_NICKNAME(HttpStatus.NOT_FOUND, "검색한 인플루언서를 찾을 수 없습니다."),
    INVALID_KEYWORD(HttpStatus.BAD_REQUEST, "검색어가 비어있을 수 없습니다"),
    INFLUENCER_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 인플루언서의 상품이 없습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "검색된 상품이 없습니다."),

    //찜
    ALREADY_FAVORITE(HttpStatus.BAD_REQUEST, "이미 찜한 상품입니다."),
    FAVORITE_NOT_FOUND(HttpStatus.NOT_FOUND, "찜 정보가 없습니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다.");
    ;

    private final HttpStatus status;
    private final String message;

    ExceptionCode(HttpStatus status, String message) {

        this.status = status;
        this.message = message;
    }
}
