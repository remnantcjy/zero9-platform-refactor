package com.zero9platform.domain.search.model;

import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;


@Getter
@RequiredArgsConstructor
public class SearchItem {

    private final Long gppId;
    private final Long userId;
    private final String image;
    private final String productName;
    private final Long price;
    private final Long viewCount;
    private final Long favoriteCount;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public static SearchItem from(GroupPurchasePost post) {
        return new SearchItem(
                post.getId(),
                post.getUser().getId(),
                post.getImage(),
                post.getProductName(),
                post.getPrice(),
                post.getViewCount(),
                post.getFavoriteCount(),
                post.getStartDate(),
                post.getEndDate()
        );
    }


}
