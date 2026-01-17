package com.zero9platform.domain.search;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.search.model.SearchResponse;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<CommonResponse<PageResponse<SearchResponse>>> searchGetHander(@RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        // 검색어 유효성 검증
        if (keyword == null || StringUtils.isBlank(keyword)) {
           throw new CustomException(ExceptionCode.INVALID_KEYWORD);
        }

        // 검색 서비스 호출
        PageResponse pageResponse = searchService.search(keyword, page, size);

        // 공통응답 객체 사용
        CommonResponse commonResponse = new CommonResponse(true, "통합 검색 결과 조회 성공", pageResponse);

        // 공통 응답 포맷으로 반환
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

}
