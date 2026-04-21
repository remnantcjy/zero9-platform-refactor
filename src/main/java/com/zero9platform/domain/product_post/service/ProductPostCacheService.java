package com.zero9platform.domain.product_post.service;

import com.amazonaws.services.s3.AmazonS3;
import com.zero9platform.common.model.CachedPageResponse;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post.model.response.ProductPostGetListResponse;
import com.zero9platform.domain.product_post.repository.ProductPostRepository;
import com.zero9platform.domain.product_post_favorite.repository.ProductPostFavoriteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductPostCacheService {

    private final ProductPostRepository productPostRepository;
    private final ProductPostFavoriteRepository productPostFavoriteRepository;
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * 'DOING' 전용 캐시 메서드
     */
    @Cacheable(value = "productPostsDoing", key = "#progressStatus + ':' + #pageable.pageNumber", unless = "#result == null")
    public CachedPageResponse<ProductPostGetListResponse> getCachedProductPosts(String progressStatus, Pageable pageable) {

        log.info("[CACHE_MISS] DB에서 데이터를 가져옵니다.");

        return fetchProductPostsFromDB(progressStatus, pageable);
    }

    /**
     * 공통 DB 조회 및 DTO 변환 로직
     */
    public CachedPageResponse<ProductPostGetListResponse> fetchProductPostsFromDB(String progressStatus, Pageable pageable) {

        Page<ProductPost> productPostsPage;

        if (progressStatus != null) {
            productPostsPage = productPostRepository.findByProgressStatusOrderByUpdatedAtDesc(progressStatus, pageable);
        } else {
            productPostsPage = productPostRepository.findAllByOrderByUpdatedAtDesc(pageable);
        }

        // DTO 변환
        List<ProductPostGetListResponse> dtoList = productPostsPage.stream().map(productPost -> {
            // 찜 개수 조회
            Long favoriteCount = productPostFavoriteRepository.countByProductPost_Id(productPost.getId());

            // 이미지 URL 생성
            String imageUrl = (productPost.getImage() != null) ? amazonS3.getUrl(bucket, productPost.getImage()).toString() : null;

            return ProductPostGetListResponse.from(productPost, imageUrl, favoriteCount);
        }).toList();

        // wrapper로 감싸서 변환 (이 결과가 Redis에 저장됨)
        return new CachedPageResponse<>(dtoList, productPostsPage.getNumber(), productPostsPage.getSize(), productPostsPage.getTotalElements(), productPostsPage.getTotalPages());
    }
}
