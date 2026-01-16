package com.zero9platform.common.enums;

import com.zero9platform.common.exception.CustomException;

import java.util.Arrays;

public enum GppApprovalStatus {

    PENDING("대기중"), APPROVED("승인됨"), REJECTED("거부됨");

    private final String description;

    GppApprovalStatus(String description) {
        this.description = description;
    }

    // 변환 메서드
    public static GppApprovalStatus from(String value) {
        return Arrays.stream(values())
                .filter(c -> c.description.equals(value))
                .findFirst()
                .orElseThrow(() ->
                        new CustomException(ExceptionCode.GPP_APPROVAL_STATUS_NOT_FOUND));
    }
}
