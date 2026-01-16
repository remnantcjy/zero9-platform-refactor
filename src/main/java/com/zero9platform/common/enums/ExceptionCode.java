package com.zero9platform.common.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {

    // 404
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    NOT_FOUND_POST(HttpStatus.NOT_FOUND, "게시물을 찾을 수 없습니다."),
    NOT_FOUND_GPP(HttpStatus.NOT_FOUND, "공동구매 게시물을 찾을 수 없습니다."),
    NOT_FOUND_GPP_SUBSCRIPTION(HttpStatus.NOT_FOUND, "팔로우한 공동구매 게시물이 없습니다."),

    // 409
    ALREADY_SUBSCRIBED_GPP(HttpStatus.CONFLICT, "이미 팔로우한 공동구매 게시물입니다.")
    ;


    private final HttpStatus status;
    private final String message;

    ExceptionCode(HttpStatus status, String message) {

        this.status = status;
        this.message = message;
    }
}
