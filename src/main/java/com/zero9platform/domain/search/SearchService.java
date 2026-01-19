package com.zero9platform.domain.search;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.search.entity.Search;
import com.zero9platform.domain.search.model.SearchItemDto;
import com.zero9platform.domain.grouppurchase_post.repository.GroupPurchasePostRepository;
import com.zero9platform.domain.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchRepository searchRepository;
    private final GroupPurchasePostRepository groupPurchasePostRepository;

    /**
     * 키워드 통합 검색
     */
    @Transactional
    public PageResponse search(String keyword, Pageable pageable) {

        // 검색어 유효성 검증
        if (keyword == null || keyword.isBlank()) {
            throw new CustomException(ExceptionCode.INVALID_KEYWORD);
        }

        Page<GroupPurchasePost> searchResult;

        // 인플루언서가 존재하는 경우 → 해당 인플루언서가 등록한 공동구매 상품 조회
        searchResult = groupPurchasePostRepository.findByUserNickname(keyword, pageable);

        // 인플루언서는 있으나 등록된 상품이 없는 경우
        if (searchResult.isEmpty()) {
            searchResult = groupPurchasePostRepository.findByProductName(keyword, pageable);
        }

        // 상품 검색 결과가 없는 경우
        if (searchResult.isEmpty()) {
            throw new CustomException(ExceptionCode.PRODUCT_NOT_FOUND);
        }

        // 검색어 로그 저장 (검색 카운트 증가)
        saveSearchKeyword(keyword);

        // 엔터티 → 응답 DTO 매핑
        Page<SearchItemDto> mappedPage = searchResult.map(SearchItemDto::from);

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
