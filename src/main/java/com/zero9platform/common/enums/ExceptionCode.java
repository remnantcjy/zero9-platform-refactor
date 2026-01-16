package com.zero9platform.common.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {

    // 400 BAD REQUEST
    GPP_INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "종료일은 시작일 이후여야 합니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    // 401 FORBIDDEN
    GPP_NO_PERMISSION(HttpStatus.FORBIDDEN, "공동구매 게시물에 대한 권한이 없습니다."),

    // 404 NOT FOUND
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
    GPP_NOT_FOUND(HttpStatus.NOT_FOUND, "공동구매 게시물을 찰을 수 없습니다."),
    GPP_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."),
    GPP_APPROVAL_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "승인상태를 찾을 수 없습니다."),
    GPP_PROGRESS_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "진행상태를 찾을 수 없습니다.")

    ;


    private final HttpStatus status;
    private final String message;

    ExceptionCode(HttpStatus status, String message) {

        this.status = status;
        this.message = message;
    }
}
