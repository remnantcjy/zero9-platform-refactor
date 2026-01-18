package com.zero9platform.domain.search;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchRequest {

    @NotBlank
    @NotNull
    @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s+\\-_.%,/()#&]+$", message = "검색어에 허용되지 않은 문자가 포함되어 있습니다.")
    private String keyword;
}