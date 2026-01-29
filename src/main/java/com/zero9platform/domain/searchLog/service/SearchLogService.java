package com.zero9platform.domain.searchLog.service;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post.repository.ProductPostRepository;
import com.zero9platform.domain.product_post_favorite.repository.ProductPostFavoriteRepository;
import com.zero9platform.domain.searchLog.entity.SearchContext;
import com.zero9platform.domain.searchLog.entity.SearchLog;
import com.zero9platform.domain.searchLog.model.SearchLogItemResponse;
import com.zero9platform.domain.searchLog.model.SearchLogListResponse;
import com.zero9platform.domain.searchLog.repository.SearchContextRepository;
import com.zero9platform.domain.searchLog.repository.SearchLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
    private final SearchContextRepository searchContextRepository;
    private final ProductPostRepository productPostRepository;
    private final ProductPostFavoriteRepository productPostFavoriteRepository;

    /**
     * 통합 검색 API
     * 검색 대상 - 공동구매 상품명, 인플루언서 활동 닉네임
     */
    @Transactional
    public Page<SearchLogItemResponse> searchLog(String keyword, String searchCondition, Pageable pageable, AuthUser authUser) {

        // searchCondition 검증
        validateSearchCondition(searchCondition);

        // 통합 검색 category(product_title, product_name, influencer), 없으면 셋 다 포함하여 검색
        Page<ProductPost> searchResult = productPostRepository.searchByKeyword(keyword, searchCondition, pageable);

        // 검색 결과 없으면 바로 반환 (불필요한 쿼리 방지)
        if (searchResult.isEmpty()) {
            return Page.empty(pageable);
        }

        // 찜 개수 조회
        Map<Long, Long> favoriteCountMap = getFavoriteCountMap(searchResult.getContent());

        // DTO 변환
        Page<SearchLogItemResponse> resultPage = searchResult.map(
                post -> SearchLogItemResponse.from(
                        post,
                        favoriteCountMap.getOrDefault(post.getId(), 0L)
                )
        );

        // 로그 저장은 트랜잭션 분리
        saveSearchLogs(keyword, searchResult.getContent(), authUser.getId());

        return resultPage;
    }

    /**
     * 인기 검색어 차트(공동구매 상품명)
     */
    @Transactional(readOnly = true)
    public List<SearchLogListResponse> searchLogProductNameList() {
        return searchLogRepository.findTopKeywords(PageRequest.of(0, 10));
    }


    /**
     * 검색 조건 검증
     */
    private void validateSearchCondition(String condition) {
        if (condition == null || condition.isBlank()) {
            return;
        }

        if (!"product_title".equals(condition) && !"product_name".equals(condition) && !"influencer".equals(condition)) {
            throw new CustomException(ExceptionCode.CATEGORY_FALSE);
        }
    }

//    /**
//     * 검색 키워드 로그 저장
//     */
//    private void searchKeywordSave(String keyword) {
//
//        // 검색어가 없으면 로그 저장하지 않음
//        if (keyword == null || keyword.isBlank()) {
//            return;
//        }
//
//        // 기존 검색어가 있으면 조회, 없으면 새로 생성
//        SearchLog searchLog = searchLogRepository.findByKeyword(keyword)
//                .orElseGet(() -> new SearchLog(keyword));
//
//        searchLog.increaseCount();
//
//        searchLogRepository.save(searchLog);
//    }


    /**
     * 검색 컨텍스트 저장 (SearchContext)
     */
    private void saveSearchLogs(String keyword, List<ProductPost> posts, Long userId) {

        // 검색어가 없으면 로그 저장하지 않음
        if (keyword == null || keyword.isBlank()) {
            return;
        }

        // 기존 검색어가 있으면 조회, 없으면 새로 생성
        SearchLog searchLog = searchLogRepository.findByKeyword(keyword)
                .orElseGet(() -> new SearchLog(keyword));

        searchLog.increaseCount();

        searchLogRepository.save(searchLog);

        // 로그인 유저만 컨텍스트 저장
        if (userId == null || posts.isEmpty()) {
            return;
        }

        List<SearchContext> contexts = posts
                .stream()
                .map(post -> new SearchContext(keyword, post.getId(), userId))
                .toList();

        searchContextRepository.saveAll(contexts);
    }

    /**
     * 찜 개수 조회
     */
    private Map<Long, Long> getFavoriteCountMap(List<ProductPost> posts) {

        List<Long> ids = posts.stream()
                .map(ProductPost::getId)
                .toList();

        return productPostFavoriteRepository.countByGppIdList(ids)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));
    }
}
