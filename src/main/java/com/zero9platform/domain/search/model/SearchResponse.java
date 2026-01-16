package com.zero9platform.domain.search.model;

import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class SearchResponse {

    private final List<SearchItem> items;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;

    public static SearchResponse from(Page<GroupPurchasePost> pageResult) {

        List<SearchItem> items = pageResult.getContent()
                .stream()
                .map(SearchItem::from)
                .toList();

        return new SearchResponse(
                items,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }
}