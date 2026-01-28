package com.zero9platform.domain.ranking;

import com.zero9platform.common.model.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/zero9")
public class RankingController {

    private final RankingService rankingService;

    /**
     * 공동구매 게시물 랭킹 (조회수 기준)
     */
    @GetMapping("/ranking/gpp")
        public ResponseEntity<CommonResponse<List<GroupPurchasePostRankingResponse>>> groupPurchasePostRanking() {

        List<GroupPurchasePostRankingResponse> response = rankingService.gppRanking();

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("공동구매 게시물 랭킹 조회 성공", response));
    }


    /**
     * 상품판매 게시물 랭킹 (찜 기준)
     */

    /**
     * 인기 검색어 랭킹 (클릭로그 or 키워드 조회수)
     */

}


