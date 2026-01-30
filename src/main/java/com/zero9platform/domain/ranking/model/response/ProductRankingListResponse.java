package com.zero9platform.domain.ranking.model.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductRankingListResponse {

    private int rank;        // 랭킹 순위
    private Long productId;  // 상품 ID
    private Long score;      // 집계 점수 (view + favorite 가중치)

    public static ProductRankingListResponse from(
            int rank,
            Long productId,
            Long score
    ) {
        return new ProductRankingListResponse(rank, productId, score);
    }
}
}
