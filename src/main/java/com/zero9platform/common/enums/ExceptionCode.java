package com.zero9platform.common.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {

    // 공통
    NO_PERMISSION(HttpStatus.FORBIDDEN, "권한이 없습니다."),

    // 사용자
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    USER_WITHDRAWN(HttpStatus.FORBIDDEN, "탈퇴한 회원입니다."),
    PASSWORD_NOT_MATCH(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    USER_IS_NOT_INFLUENCER(HttpStatus.FORBIDDEN, "해당 사용자는 인플루언서가 아닙니다."),
    INFLUENCER_NOT_APPROVED(HttpStatus.FORBIDDEN, "승인되지 않은 인플루언서입니다."),
    LOGINID_EXIST(HttpStatus.CONFLICT, "중복되는 아이디가 존재합니다."),
    EMAIL_EXIST(HttpStatus.CONFLICT, "중복되는 이메일이 존재합니다."),
    PHONE_EXIST(HttpStatus.CONFLICT, "중복되는 핸드폰번호가 존재합니다."),
    NICKNAME_EXIST(HttpStatus.CONFLICT, "중복되는 닉네임이 존재합니다."),

    // 게시물
    NOT_FOUND_POST(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),

    // jwt
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "토큰을 찾을 수 없습니다."),
    ;


    private final HttpStatus status;
    private final String message;

    ExceptionCode(HttpStatus status, String message) {

        this.status = status;
        this.message = message;
    }
}
