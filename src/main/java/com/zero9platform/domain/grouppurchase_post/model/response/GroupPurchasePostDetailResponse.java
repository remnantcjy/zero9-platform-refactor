package com.zero9platform.domain.grouppurchase_post.model.response;

import com.zero9platform.common.enums.Category;
import com.zero9platform.common.enums.GppProgressStatus;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GroupPurchasePostDetailResponse {

    private Long id;
    private String productName;
    private Long userId;
    private String content;
    private String image;
    private Long viewCount;
    private Long price;
    private String linkUrl;
    private String category;
    private String gppProgressStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static GroupPurchasePostDetailResponse from(GroupPurchasePost groupPurchasePostGetDetailResponse) {

        Category category = Category.valueOf(groupPurchasePostGetDetailResponse.getCategory());
        GppProgressStatus gppProgressStatus = GppProgressStatus.valueOf(groupPurchasePostGetDetailResponse.getGppProgressStatus());

        return new GroupPurchasePostDetailResponse(
                groupPurchasePostGetDetailResponse.getId(),
                groupPurchasePostGetDetailResponse.getProductName(),
                groupPurchasePostGetDetailResponse.getUser().getId(),
                groupPurchasePostGetDetailResponse.getContent(),
                groupPurchasePostGetDetailResponse.getImage(),
                groupPurchasePostGetDetailResponse.getViewCount(),
                groupPurchasePostGetDetailResponse.getPrice(),
                groupPurchasePostGetDetailResponse.getLinkUrl(),
                category.getDescription(),
                gppProgressStatus.getDescription(),
                groupPurchasePostGetDetailResponse.getStartDate(),
                groupPurchasePostGetDetailResponse.getEndDate(),
                groupPurchasePostGetDetailResponse.getCreatedAt(),
                groupPurchasePostGetDetailResponse.getUpdatedAt()
        );
    }

}
