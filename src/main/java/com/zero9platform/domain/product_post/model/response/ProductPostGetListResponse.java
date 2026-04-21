package com.zero9platform.domain.product_post.model.response;

import com.zero9platform.domain.product_post.entity.ProductPost;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductPostGetListResponse {

    private Long id;
    private String title;
    private String name;
    private Long originalPrice;
    private String image;
    private Long favoriteCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProductPostGetListResponse from(ProductPost productPost, String image, Long favoriteCount) {

        return new ProductPostGetListResponse(
                productPost.getId(),
                productPost.getTitle(),
                productPost.getName(),
                productPost.getOriginalPrice(),
                image,
                favoriteCount,
                productPost.getCreatedAt(),
                productPost.getUpdatedAt()
        );
    }
}