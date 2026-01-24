package com.zero9platform.domain.product_post_favorite.model.response;

import com.zero9platform.domain.product_post_favorite.entity.ProductPostFavorite;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ProductPostFavoriteGetResponse {

    private final Long gppId;
    private final Long userId;
    private final String productName;
    private final LocalDateTime createdAt;

    public static ProductPostFavoriteGetResponse from(ProductPostFavorite productPostFavorite) {
        return new ProductPostFavoriteGetResponse(
                productPostFavorite.getGroupPurchasePost().getId(),
                productPostFavorite.getUser().getId(),
                productPostFavorite.getGroupPurchasePost().getProductName(),
                productPostFavorite.getGroupPurchasePost().getCreatedAt());
    }
}
