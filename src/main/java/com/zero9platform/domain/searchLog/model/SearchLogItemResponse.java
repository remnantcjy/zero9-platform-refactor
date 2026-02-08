package com.zero9platform.domain.searchLog.model;

import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.product_post.entity.ProductPost;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class SearchLogItemResponse {

    private final Long postId;         // 통합 ID
    private final String postType;     // "PRODUCT" 또는 "GPP" 구분
    private final Long userId;
    private final String nickname;
    private final String image;
    private final String title;        // productPostTitle -> title (범용)
    private final Long price;          // productPrice -> price (범용)
    private final Long favoriteCount;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    // ProductPost용
    public static SearchLogItemResponse from(ProductPost productPost, Long favoriteCount) {
        return new SearchLogItemResponse(
                productPost.getId(),
                "ProductPost",
                productPost.getUser().getId(),
                productPost.getUser().getNickname(),
                productPost.getImage(),
                productPost.getTitle(),
                productPost.getOriginalPrice(),
                favoriteCount,
                productPost.getStartDate(),
                productPost.getEndDate()
        );
    }

    // GroupPurchasePost용
    public static SearchLogItemResponse from(GroupPurchasePost gpp) {
        return new SearchLogItemResponse(
                gpp.getId(),
                "GroupPurchasePost",
                gpp.getUser().getId(),
                gpp.getUser().getNickname(),
                gpp.getImage(),
                gpp.getProductName(),
                gpp.getPrice(),
                null,
                gpp.getStartDate(),
                gpp.getEndDate()
        );
    }


}
