package com.zero9platform.common.model;

import lombok.Getter;

@Getter
public class CommonResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;

    public CommonResponse(boolean success, String message, T data) {

        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> CommonResponse<T> success(String message, T data) {

        return new CommonResponse<>(true, message, data);
    }

    public static <T> CommonResponse<T> fail(String message) {

        return new CommonResponse<>(false, message, null);
    }
}
