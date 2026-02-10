package com.zero9platform.domain.searchLog.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class RecentSearchResponse {

    private final String keyword;
    private final LocalDateTime searchedAt;

}