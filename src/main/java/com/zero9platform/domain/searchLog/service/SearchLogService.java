package com.zero9platform.domain.searchLog.service;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.grouppurchase_post.repository.GroupPurchasePostRepository;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post.repository.ProductPostRepository;
import com.zero9platform.domain.product_post_favorite.repository.ProductPostFavoriteRepository;
import com.zero9platform.domain.ranking.service.RankingCounter;
import com.zero9platform.domain.searchLog.entity.SearchLog;
import com.zero9platform.domain.searchLog.model.SearchLogItemResponse;
import com.zero9platform.domain.searchLog.repository.SearchLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchLogService {

    private final SearchLogRepository searchLogRepository;
    private final ProductPostRepository productPostRepository;
    private final GroupPurchasePostRepository groupPurchasePostRepository;
    private final ProductPostFavoriteRepository productPostFavoriteRepository;
    private final RankingCounter rankingCounter;


    /**
     * 통합 검색 API
     * 검색 대상 - 공동구매 상품명, 인플루언서 활동 닉네임
     */
    @Transactional
    public Page<SearchLogItemResponse> searchLog(String keyword, String searchCondition, Pageable pageable, AuthUser authUser) {

        // 검색 조건 검증 (허용되지 않은 조건 차단)
        validateSearchCondition(searchCondition);

        // ProductPost 검색
        Page<ProductPost> productResults = productPostRepository.searchByKeyword(keyword, searchCondition, pageable);

        // GroupPurchasePost 검색
        Page<GroupPurchasePost> gppResults = groupPurchasePostRepository.searchByKeyword(keyword, searchCondition, pageable);

        // 두 결과 리스트를 SearchLogItemResponse로 변환하여 합치기
        List<SearchLogItemResponse> combinedContent = new ArrayList<>();

        // ProductPost 변환 및 추가
        if (productResults.hasContent()) {
            Map<Long, Long> productPostMap = favoriteCountMap(productResults.getContent());
            combinedContent.addAll(productResults.stream()
                    .map(post -> SearchLogItemResponse.from(post, productPostMap.getOrDefault(post.getId(), 0L)))
                    .toList());
        }

        // GroupPurchasePost 변환 및 추가 (GPP용 from 메서드가 Response DTO에 필요함)
        if (gppResults.hasContent()) {
            combinedContent.addAll(gppResults.stream()
                    .map(gpp-> SearchLogItemResponse.from(gpp))
                    .toList());
        }

        // 검색 결과가 아예 없으면 빈 페이지 반환
        if (productResults.isEmpty() && gppResults.isEmpty()) {return Page.empty(pageable);}

        // 검색 로그 저장 및 검색 랭킹 카운터 증가
        if (keyword != null && !keyword.isBlank()) {
            rankingCounter.increaseKeyword(keyword);
            Long userId = (authUser != null) ? authUser.getId() : null;
            saveSearchLogs(keyword, userId);
        }

        long totalElements = productResults.getTotalElements() + gppResults.getTotalElements();
        return new PageImpl<>(combinedContent, pageable, totalElements);
    }

    /**
     * 검색 조건 검증
     */
    @Transactional
    public void validateSearchCondition(String condition) {

        if (condition == null || condition.isBlank()) {return;}
        List<String> allowedConditions = List.of("product_title", "product_name", "influencer", "content");
        if (!allowedConditions.contains(condition)) {
            throw new CustomException(ExceptionCode.CATEGORY_FALSE);
        }
    }

    /**
     * 검색 로그 저장
     */
    @Transactional
    public void saveSearchLogs(String keyword, Long userId) {

        // 검색어가 없으면 로그 저장하지 않음
        if (keyword == null || keyword.isBlank()) {return;}

        // 검색어 로그 저장 (카운트 증가)
        SearchLog searchLog = searchLogRepository.findByKeyword(keyword)
                .orElseGet(() -> new SearchLog(keyword));

        // 검색 횟수 증가
        searchLog.increaseCount();
        searchLogRepository.save(searchLog);
    }

    /**
     * 찜 개수 조회
     */
    @Transactional
    public Map<Long, Long> favoriteCountMap(List<ProductPost> posts) {

        // 게시물 ID 추출
        List<Long> ids = posts.stream()
                .map(ProductPost::getId)
                .toList();

        // DB 집계 결과를 Map 형태로 변환
        return productPostFavoriteRepository.countByGppIdList(ids)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],  // productPostId
                        row -> (Long) row[1]   // favoriteCount
                ));
    }
}
