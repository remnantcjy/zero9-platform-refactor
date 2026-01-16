package com.zero9platform.domain.search;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.search.model.SearchResponse;
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

    //zero9/searches?keyword={검색어}
    @GetMapping("/searches")
    public ResponseEntity<CommonResponse<SearchResponse>> searchGetHander(@RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        PageResponse pageResponse = searchService.search(keyword, page, size);

        CommonResponse commonResponse = new CommonResponse(true, "통합 검색 결과 조회 성공", pageResponse);

        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

}
