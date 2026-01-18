package com.zero9platform.common.enums;

import com.zero9platform.common.exception.CustomException;

import java.util.Arrays;

public enum GppProgressStatus {

    READY("준비중"), DOING("진행중"), END("종료됨");

    private final String description;

    GppProgressStatus(String description) {
        this.description = description;
    }

    // 변환 메서드
    public static GppProgressStatus from(String value) {
        return Arrays.stream(values())
                .filter(c -> c.description.equals(value))
                .findFirst()
                .orElseThrow(() ->
                        new CustomException(ExceptionCode.GPP_PROGRESS_STATUS_NOT_FOUND));
    }
}
