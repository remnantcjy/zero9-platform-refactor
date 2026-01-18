package com.zero9platform.common.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {

    // 공통
    LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    NO_PERMISSION(HttpStatus.FORBIDDEN, "권한이 없습니다."),

    // 사용자
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    USER_WITHDRAWN(HttpStatus.FORBIDDEN, "탈퇴한 회원입니다."),
    PASSWORD_NOT_MATCH(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    USER_IS_NOT_INFLUENCER(HttpStatus.FORBIDDEN, "해당 사용자는 인플루언서가 아닙니다."),
    INFLUENCER_SOCIAL_LINK_REQUIRED(HttpStatus.BAD_REQUEST, "인플루언서 계정은 소셜 링크 입력이 필수입니다."),
    INFLUENCER_NOT_APPROVED(HttpStatus.FORBIDDEN, "승인되지 않은 인플루언서입니다."),
    LOGINID_EXIST(HttpStatus.CONFLICT, "중복되는 아이디가 존재합니다."),
    EMAIL_EXIST(HttpStatus.CONFLICT, "중복되는 이메일이 존재합니다."),
    PHONE_EXIST(HttpStatus.CONFLICT, "중복되는 핸드폰번호가 존재합니다."),
    NICKNAME_EXIST(HttpStatus.CONFLICT, "중복되는 닉네임이 존재합니다."),
    NOT_FOUND_POST(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),

    // jwt
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "토큰을 찾을 수 없습니다."),

    // GPP - 400 BAD REQUEST
    GPP_INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "종료일은 시작일 이후여야 합니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    // GPP - 401 FORBIDDEN
    GPP_NO_PERMISSION(HttpStatus.FORBIDDEN, "공동구매 게시물에 대한 권한이 없습니다."),

    // GPP - 404 NOT FOUND
    GPP_NOT_FOUND(HttpStatus.NOT_FOUND, "공동구매 게시물을 찰을 수 없습니다."),
    GPP_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."),
    GPP_APPROVAL_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "승인상태를 찾을 수 없습니다."),
    GPP_PROGRESS_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "진행상태를 찾을 수 없습니다."),

    //검색
    NOT_FOUND_NICKNAME(HttpStatus.NOT_FOUND, "검색한 인플루언서를 찾을 수 없습니다."),
    INVALID_KEYWORD(HttpStatus.BAD_REQUEST, "검색어가 비어있을 수 없습니다"),
    INFLUENCER_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 인플루언서의 상품이 없습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "검색된 상품이 없습니다."),

    ;

    private final HttpStatus status;
    private final String message;

    ExceptionCode(HttpStatus status, String message) {

        this.status = status;
        this.message = message;
    }
}
