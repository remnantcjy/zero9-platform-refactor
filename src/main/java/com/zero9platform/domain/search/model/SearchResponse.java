package com.zero9platform.domain.search.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class SearchResponse {

    private final List<SearchItem> items;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
}