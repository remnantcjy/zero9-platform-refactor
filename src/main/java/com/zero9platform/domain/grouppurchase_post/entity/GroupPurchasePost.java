package com.zero9platform.domain.grouppurchase_post.entity;

import com.zero9platform.common.entity.BaseEntity;
import com.zero9platform.common.enums.Category;
import com.zero9platform.common.enums.GppApprovalStatus;
import com.zero9platform.common.enums.GppProgressStatus;
import com.zero9platform.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "group_purchase_posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupPurchasePost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private Long viewCount = 1L;

    @Column(nullable = false)
    private Long price = 1000L;

    @Column(nullable = false)
    private String linkUrl;

    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private GppApprovalStatus gppApprovalStatus;

    @Column(nullable = false)
    private GppProgressStatus gppProgressStatus;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column
    private LocalDateTime deletedAt;

    @Column(nullable = false)
    private Long favoriteCount = 1L;


    public GroupPurchasePost(
            User user,
            String productName,
            String content,
            String image,
            Long price,
            String linkUrl,
            Category category,
            GppApprovalStatus approvalStatus,
            GppProgressStatus progressStatus,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        this.user = user;
        this.productName = productName;
        this.content = content;
        this.image = image;
        this.price = price;
        this.linkUrl = linkUrl;
        this.category = category;
        this.gppApprovalStatus = approvalStatus;
        this.gppProgressStatus = progressStatus;
        this.startDate = startDate;
        this.endDate = endDate;
        this.viewCount = 0L;
        this.favoriteCount = 0L;
    }
}
