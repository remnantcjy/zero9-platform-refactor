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
import com.zero9platform.domain.searchLog.elasticsearch.SearchDocument;
import com.zero9platform.domain.searchLog.entity.SearchLog;
import com.zero9platform.domain.searchLog.model.SearchLogItemResponse;
import com.zero9platform.domain.searchLog.repository.SearchDocumentRepository;
import com.zero9platform.domain.searchLog.repository.SearchLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.lettuce.core.GeoArgs.Unit.m;

@Log4j2
@Service
@RequiredArgsConstructor
public class SearchLogService {

    private final SearchLogRepository searchLogRepository;
    private final ProductPostRepository productPostRepository;
    private final GroupPurchasePostRepository groupPurchasePostRepository;
    private final ProductPostFavoriteRepository productPostFavoriteRepository;
    private final SearchDocumentRepository searchDocumentRepository; // ES 레포지토리
    private final ElasticsearchOperations elasticsearchOperations; // 정교한 쿼리용
    private final RankingCounter rankingCounter;


    /**
     * 통합 검색 API
     * 검색 대상 - 공동구매 상품명, 인플루언서 활동 닉네임
     */
    @Transactional
    public Page<SearchLogItemResponse> searchLog(String keyword, String postType, Pageable pageable, AuthUser authUser) {

        // 검색 조건 검증 (허용되지 않은 조건 차단)
        validateSearchCondition(postType);

        // keyword 없으면 빈 페이지
        if (keyword == null || keyword.isBlank()) {
            return Page.empty(pageable);
        }

        // ES용 쿼리 빌드 (DB 레포지토리 호출 대신)
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.multiMatch(m -> {
                    List<String> targetFields;
                    if ("product_title".equals(postType)) targetFields = List.of("title", "content");
                    else if ("content".equals(postType)) targetFields = List.of("content");
                    else if ("influencer".equals(postType)) targetFields = List.of("nickname");
                    else targetFields = List.of("title", "content", "nickname"); // 전체 검색

                    return m.fields(targetFields)
                            .query(keyword)
                            .fuzziness("1");
                }))
                .withPageable(pageable)
                .build();

        // ES 검색 실행
        SearchHits<SearchDocument> hits = elasticsearchOperations.search(query, SearchDocument.class);

        // 검색 결과에서 ID들만 추출
        List<Long> postIds = hits.getSearchHits().stream()
                .map(hit -> Long.parseLong(hit.getContent().getId().split("_")[1]))
                .toList();

        // 찜 개수 맵 가져오기
        Map<Long, Long> favCounts = favoriteCountMap(postIds);

        // ES 결과(SearchDocument)를 Response DTO로 변환
        List<SearchLogItemResponse> contents = hits.getSearchHits().stream()
                .map(hit -> {
                    SearchDocument doc = hit.getContent();
                    Long originalId = Long.parseLong(doc.getId().split("_")[1]);

                    String matchType = "기타"; // 기본값
                    if ("influencer".equals(postType)) {matchType = "인플루언서 매칭";
                    } else if ("product_title".equals(postType)) {matchType = "제목 매칭";
                    } else if ("content".equals(postType)) {matchType = "내용 매칭";
                    }

                    // 전체 검색(searchCondition이 null이거나 ALL인 경우)일 때만 데이터 기반으로 추론
                    else {
                        if (doc.getNickname() != null && doc.getNickname().contains(keyword)) {matchType = "인플루언서 매칭";
                        } else if (doc.getTitle() != null && doc.getTitle().contains(keyword)) {matchType = "제목 매칭";
                        } else if (doc.getContent() != null && doc.getContent().contains(keyword)) {matchType = "내용 매칭";}
                    }
                    return SearchLogItemResponse.from(doc, matchType, favCounts.getOrDefault(originalId, 0L));
                })
                .toList();


//        // ProductPost 검색
//        Page<ProductPost> productResults = productPostRepository.searchByKeyword(keyword, searchCondition, pageable);
//        // GroupPurchasePost 검색
//        Page<GroupPurchasePost> gppResults = groupPurchasePostRepository.searchByKeyword(keyword, searchCondition, pageable);
//
//        // 두 결과 리스트를 SearchLogItemResponse로 변환하여 합치기
//        List<SearchLogItemResponse> combinedContent = new ArrayList<>();
//
//        // ProductPost 변환 및 추가
//        if (productResults.hasContent()) {
//            Map<Long, Long> productPostMap = favoriteCountMap(productResults.getContent());
//            combinedContent.addAll(productResults.stream()
//                    .map(post -> SearchLogItemResponse.from(post, productPostMap.getOrDefault(post.getId(), 0L)))
//                    .toList());
//        }
//
//        // GroupPurchasePost 변환 및 추가 (GPP용 from 메서드가 Response DTO에 필요함)
//        if (gppResults.hasContent()) {
//            combinedContent.addAll(gppResults.stream()
//                    .map(gpp -> SearchLogItemResponse.from(gpp))
//                    .toList());
//        }
//
//        // 검색 결과가 아예 없으면 빈 페이지 반환
//        if (productResults.isEmpty() && gppResults.isEmpty()) {
//            return Page.empty(pageable);
//        }

        // 검색 로그 저장 및 검색 랭킹 카운터 증가
        if (!keyword.isBlank()) {
            rankingCounter.increaseKeyword(keyword);
            Long userId = (authUser != null) ? authUser.getId() : null;
            saveSearchLogs(keyword, userId);
        }

        return new PageImpl<>(contents, pageable, hits.getTotalHits());
    }

    /**
     * 검색 조건 검증
     */
    @Transactional
    public void validateSearchCondition(String condition) {

        if (condition == null || condition.isBlank()) {
            return;
        }
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
        if (keyword == null || keyword.isBlank()) {
            return;
        }

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
    public Map<Long, Long> favoriteCountMap(List<Long> posts) {

        if (posts.isEmpty()) return Map.of();

        // DB 집계 결과를 Map 형태로 변환
        return productPostFavoriteRepository.countByGppIdList(posts)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],  // productPostId
                        row -> (Long) row[1]   // favoriteCount
                ));
    }

    /**
     * DB의 모든 데이터를 ES로 밀어넣기 (초기 1회 및 데이터 보정용)
     */
    @Transactional(readOnly = true)
    public void bulkIndexing() {
        // 1. 모든 데이터 가져오기
        List<ProductPost> products = productPostRepository.findAll();
        List<GroupPurchasePost> gpps = groupPurchasePostRepository.findAll();

        // 2. 통합 리스트 생성
        List<SearchDocument> allDocs = new ArrayList<>();

        products.forEach(post -> allDocs.add(SearchDocument.builder()
                .id("PRODUCT_" + post.getId())
                .postType("PRODUCT")
                .title(post.getTitle())
                .content(post.getContent())
                .nickname(post.getUser().getNickname())
                .price(post.getOriginalPrice())
                .image(post.getImage())
                .startDate(post.getStartDate())
                .endDate(post.getEndDate())
                .userId(post.getUser().getId()) // 이 줄 추가
                .image(post.getImage())
                .build()));

        gpps.forEach(gpp -> allDocs.add(SearchDocument.builder()
                .id("GPP_" + gpp.getId())
                .postType("GPP")
                .title(gpp.getProductName())
                .content(gpp.getContent())
                .nickname(gpp.getUser().getNickname())
                .price(gpp.getPrice())
                .image(gpp.getImage())
                .startDate(gpp.getStartDate())
                .endDate(gpp.getEndDate())
                .build()));

        // 3. 분할 저장 (핵심: 413 에러 방지)
        int batchSize = 1000; // 한 번에 1000개씩 전송
        for (int i = 0; i < allDocs.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, allDocs.size());
            List<SearchDocument> batch = allDocs.subList(i, endIndex);
            searchDocumentRepository.saveAll(batch); // 쪼개서 전송
            log.info("✅ 인덱싱 진행 중... (" + endIndex + "/" + allDocs.size() + ")");
        }

        log.info("🚀 최종 인덱싱 완료!");
    }
}
