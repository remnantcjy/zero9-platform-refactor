package com.zero9platform.domain.product_post.model.response;

import com.zero9platform.domain.product.entity.Product;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ProductPostGetDetailResponse {

    private final Long id;
    private final Long userId;
    private final Long productId;
    private final String title;
    private final String content;
    private final Long stock;
    private final String image;
    private final String category;
    private final String productPostProgressStatus;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static ProductPostGetDetailResponse from(ProductPost productPost) {

        return new ProductPostGetDetailResponse(
                productPost.getId(),
                productPost.getUser().getId(),
                productPost.getProduct().getId(),
                productPost.getTitle(),
                productPost.getContent(),
                productPost.getStock(),
                productPost.getImage(),
                productPost.getCategory().name(),
                productPost.getProductPostProgressStatus().name(),
                productPost.getStartDate(),
                productPost.getEndDate(),
                productPost.getCreatedAt(),
                productPost.getUpdatedAt()
        );
    }
}
