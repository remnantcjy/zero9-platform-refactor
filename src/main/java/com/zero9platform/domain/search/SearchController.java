package com.zero9platform.domain.search;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.search.model.SearchItemDto;
import com.zero9platform.domain.search.model.request.SearchRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/zero9")
public class SearchController {

    private final SearchService searchService;

    /**
     * 통합 검색 API
     * 검색 대상
     * - 공동구매 상품명
     * - 인플루언서 활동 닉네임
     */
    @GetMapping("/searches")
    public ResponseEntity<CommonResponse<PageResponse<SearchItemDto>>> searchGetHandler(@Valid @RequestBody SearchRequest rRequest, Pageable pageable) {

        // 검증된 검색어 키워드 추출
        String keyword = rRequest.getKeyword();

        // 검색 서비스 호출
        PageResponse pageResponse = searchService.search(keyword, pageable);

        // 공통응답 객체 사용
        CommonResponse commonResponse = new CommonResponse(true, "통합 검색 결과 조회 성공", pageResponse);

        // 공통 응답 포맷으로 반환
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

}
