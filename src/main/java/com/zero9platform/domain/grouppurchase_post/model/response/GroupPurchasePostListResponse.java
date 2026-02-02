package com.zero9platform.domain.grouppurchase_post.model.response;

import com.zero9platform.common.enums.Category;
import com.zero9platform.common.enums.GppProgressStatus;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GroupPurchasePostListResponse {

    private Long id;
    private String productName;
    private Long userId;
    private String image;
    private Long viewCount;
    private Long favoriteCount; // 추후 좋아요 기능 연동
    private Long price;
    private String category;
    private String gppProgressStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static GroupPurchasePostListResponse from(GroupPurchasePost groupPurchasePostGetDetailResponse) {

        Category category = Category.valueOf(groupPurchasePostGetDetailResponse.getCategory());
        GppProgressStatus gppProgressStatus = GppProgressStatus.valueOf(groupPurchasePostGetDetailResponse.getGppProgressStatus());

        return new GroupPurchasePostListResponse(
                groupPurchasePostGetDetailResponse.getId(),
                groupPurchasePostGetDetailResponse.getProductName(),
                groupPurchasePostGetDetailResponse.getUser().getId(),
                groupPurchasePostGetDetailResponse.getImage(),
                groupPurchasePostGetDetailResponse.getViewCount(),
                0L, // favoriteCount 임시값 (추후 연관관계 추가 시 수정)
                groupPurchasePostGetDetailResponse.getPrice(),
                category.getDescription(),
                gppProgressStatus.getDescription(),
                groupPurchasePostGetDetailResponse.getStartDate(),
                groupPurchasePostGetDetailResponse.getEndDate(),
                groupPurchasePostGetDetailResponse.getCreatedAt(),
                groupPurchasePostGetDetailResponse.getUpdatedAt()
        );
    }

}
