package com.zero9platform.domain.ranking;

import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GroupPurchasePostRankingResponse {

    private final int rank;
    private final Long groupPurchasePostId;
    private final String groupPurchasePostTitle;
    private final Long viewCount;

    public static GroupPurchasePostRankingResponse from(int rank, GroupPurchasePost post) {
        return new GroupPurchasePostRankingResponse(
                rank,
                post.getId(),
                post.getProductName(),
                post.getViewCount()
        );
    }
}
