package com.zero9platform.domain.search;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.gpp_favorite.repository.GppFavoriteRepository;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.search.entity.Search;
import com.zero9platform.domain.search.model.SearchItemResponse;
import com.zero9platform.domain.grouppurchase_post.repository.GroupPurchasePostRepository;
import com.zero9platform.domain.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchRepository searchRepository;
    private final GroupPurchasePostRepository groupPurchasePostRepository;
    private final GppFavoriteRepository gppFavoriteRepository;

    /**
     * 키워드 통합 검색
     */
    @Transactional
    public PageResponse<SearchItemResponse> search(String keyword, String searchCondition, Pageable pageable) {

        if (!"product".equals(searchCondition) && !"influencer".equals(searchCondition)) {
            throw new CustomException(ExceptionCode.CATEGORY_FALSE);
        }

        // 검색어 유효성 검증
        if (keyword == null || keyword.isBlank()) {
            throw new CustomException(ExceptionCode.INVALID_KEYWORD);
        }

//        if (searchCondition != null &&
//                !"product".equals(searchCondition) &&
//                !"influencer".equals(searchCondition)) {
//            throw new CustomException(ExceptionCode.CATEGORY_FALSE);
//        }

        Page<GroupPurchasePost> searchResult;

        // 통합 검색
        // category가 product면 상품명, influencer면 인플루언서 닉네임, 없으면 둘 다 포함하여 검색
        searchResult = groupPurchasePostRepository.searchByKeyword(keyword, searchCondition, pageable);

        // 최종 검색 결과가 없는 경우 예외 처리
        if (searchResult.isEmpty()) {
            throw new CustomException(ExceptionCode.PRODUCT_NOT_FOUND);
        }

        // 검색어 로그 저장 (검색 카운트 증가)
        saveSearchKeyword(keyword);

        // searchResult로부터 gppId 추출
        List<Long> gppIdList = new ArrayList<>();
        for (GroupPurchasePost post : searchResult) {
            gppIdList.add(post.getId());
        }

        // 찜 개수 조회 -> Map 변환
        Map<Long, Long> favoriteCountMap = new HashMap<>();
        for (Object[] row : gppFavoriteRepository.countByGppIdList(gppIdList)) {
            favoriteCountMap.put((Long) row[0], (Long) row[1]);
        }

        // DTO 변환
        List<SearchItemResponse> dtoList = new ArrayList<>();
        for (GroupPurchasePost post : searchResult.getContent()) {
            dtoList.add(SearchItemResponse.from(post, favoriteCountMap.getOrDefault(post.getId(), 0L)));
        }

        Page<SearchItemResponse> mappedPage = new PageImpl<>(dtoList, searchResult.getPageable(), searchResult.getTotalElements());

        // 공통 페이징 응답 객체로 변환
        return PageResponse.from(mappedPage);
    }

    /**
     * 검색 키워드 로그 저장
     */
    private void saveSearchKeyword(String keyword) {

        // 검색어가 없으면 로그 저장하지 않음
        if (keyword == null || keyword.isBlank()) {
            return;
        }

        // 기존 검색어가 있으면 조회, 없으면 새로 생성
        Search search = searchRepository.findByKeyword(keyword)
                .orElseGet(() -> new Search(keyword));

        // 검색 횟수 증가
        search.increaseCount();

        //DB 저장
        searchRepository.save(search);
    }
}
