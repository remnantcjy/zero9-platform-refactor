package com.zero9platform.domain.searchLog.model;

import com.zero9platform.domain.searchLog.elasticsearch.SearchDocument;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class SearchLogItemResponse {

    private final Long postId;
    private final String postType;
    private final String matchType;
    private final Long userId;
    private final String nickname;
    private final String image;
    private final String title;
    private final Long price;
    private final Long favoriteCount;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public static SearchLogItemResponse from(SearchDocument doc, String matchType, Long favoriteCount) {
        Long originalId = Long.parseLong(doc.getId().split("_")[1]);
        return new SearchLogItemResponse(
                originalId,
                doc.getPostType(),
                matchType,
                doc.getUserId(),
                doc.getNickname(),
                doc.getImage(),
                doc.getTitle(),
                doc.getPrice(),
                favoriteCount,
                doc.getStartDate(),
                doc.getEndDate()
        );
    }

//    // ProductPost용
//    public static SearchLogItemResponse from(ProductPost productPost, Long favoriteCount) {
//        return new SearchLogItemResponse(
//                productPost.getId(),
//                "ProductPost",
//                productPost.getUser().getId(),
//                productPost.getUser().getNickname(),
//                productPost.getImage(),
//                productPost.getTitle(),
//                productPost.getOriginalPrice(),
//                favoriteCount,
//                productPost.getStartDate(),
//                productPost.getEndDate()
//        );
//    }
//
//    // GroupPurchasePost용
//    public static SearchLogItemResponse from(GroupPurchasePost gpp) {
//        return new SearchLogItemResponse(
//                gpp.getId(),
//                "GroupPurchasePost",
//                gpp.getUser().getId(),
//                gpp.getUser().getNickname(),
//                gpp.getImage(),
//                gpp.getProductName(),
//                gpp.getPrice(),
//                null,
//                gpp.getStartDate(),
//                gpp.getEndDate()
//        );
//    }


}
