package com.zero9platform.common.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {

    NOT_FOUND_POST(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),

    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),


    ;


    private final HttpStatus status;
    private final String message;

    ExceptionCode(HttpStatus status, String message) {

        this.status = status;
        this.message = message;
    }
}
