package com.zero9platform.domain.search.model;

import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class SearchItemResponse {

    private final Long gppId;
    private final Long userId;
    private final String nickname;
    private final String image;
    private final String productName;
    private final Long price;
    private final Long viewCount;
    private final Long favoriteCount;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public static SearchItemResponse from(GroupPurchasePost post, Long favoriteCount) {
        return new SearchItemResponse(
                post.getId(),
                post.getUser().getId(),
                post.getUser().getNickname(),
                post.getImage(),
                post.getProductName(),
                post.getPrice(),
                post.getViewCount(),
                favoriteCount,
                post.getStartDate(),
                post.getEndDate()
        );
    }


}
