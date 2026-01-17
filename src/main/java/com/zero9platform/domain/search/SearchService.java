package com.zero9platform.domain.search;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.search.entity.Search;
import com.zero9platform.domain.search.model.SearchItem;
import com.zero9platform.domain.search.repository.GroupPurchasePostRepository;
import com.zero9platform.domain.search.repository.SearchRepository;
import com.zero9platform.domain.user_influencer.entity.UserInfluencer;
import com.zero9platform.domain.user_influencer.entity.UserInfluencerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchRepository searchRepository;
    private final UserInfluencerRepository userInfluencerRepository;
    private final GroupPurchasePostRepository groupPurchasePostRepository;


    /**
     * 키워드 통합 검색
     */
    @Transactional
    public PageResponse search(String keyword, int page, int size) {

        // NPE 방어
        if (keyword == null || keyword.isBlank()) {
            throw new CustomException(ExceptionCode.INVALID_KEYWORD);
        }

        //페이징 준비
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<GroupPurchasePost> searchResult;

        //인플루언서 이름 찾기
        Optional<UserInfluencer> influencerInfo =
                userInfluencerRepository.findByNickname(keyword);

        if (influencerInfo.isPresent()) {
            //인플루언서가 등록한 상품 조회
            searchResult = groupPurchasePostRepository.findByUser(
                    influencerInfo.get().getUser(),
                    pageRequest
            );

            // 인플루언서는 있는데 상품이 없음
            if (searchResult.isEmpty()) {
                throw new CustomException(ExceptionCode.INFLUENCER_POST_NOT_FOUND);
            }

        } else {
            // 싱품명 검색
            searchResult = groupPurchasePostRepository.search(keyword, pageRequest);
            // 상품 검색 결과 없음
            if (searchResult.isEmpty()) {
                throw new CustomException(ExceptionCode.PRODUCT_NOT_FOUND);
            }
        }

        //키워드 카운터 1 증가후 저장하기
        saveSearchKeyword(keyword);

        Page<SearchItem> mappedPage = searchResult.map(SearchItem::from);

        //응답 객체로 전달
        return PageResponse.from(mappedPage);
    }


    /**
     * 검색 키워드 저장
     */
    private void saveSearchKeyword(String keyword) {

        // NPE 방어(검색어 로그 저장 안 함)
        if (keyword == null || keyword.isBlank()) {
            return;
        }

        //동일한 키워드가 있으면 엔터티 등록
        Search search = searchRepository.findByKeyword(keyword)
                .orElseGet(() -> new Search(keyword));

        //해당 검색어에 카운터 1 증가
        search.increaseCount();

        //DB 저장
        searchRepository.save(search);
    }
}
