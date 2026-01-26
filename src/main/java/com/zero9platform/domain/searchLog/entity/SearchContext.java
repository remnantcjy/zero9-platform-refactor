package com.zero9platform.domain.searchLog.entity;

import com.zero9platform.domain.product_post.entity.ProductPost;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchContext {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String keyword;

//    @ManyToOne(fetch =  FetchType.LAZY)
//    @JoinColumn(name = "product_post_id")
//    private ProductPost productPost;

    private Long productPostId;

    private LocalDateTime createdAt;

    public SearchContext(String keyword, Long productPostId) {
        this.keyword = keyword;
        this.productPostId = productPostId;
        this.createdAt = LocalDateTime.now();
    }
}