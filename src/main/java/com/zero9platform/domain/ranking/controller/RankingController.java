package com.zero9platform.domain.ranking.controller;

import com.zero9platform.common.enums.RankingPeriod;
import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.ranking.service.RankingService;
import com.zero9platform.domain.ranking.model.response.GroupPurchasePostRankingListResponse;
import com.zero9platform.domain.ranking.model.response.ProductPostFavoriteRankingListResponse;
import com.zero9platform.domain.ranking.model.response.SearchLogRankingListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
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
    public ResponseEntity<CommonResponse<List<GroupPurchasePostRankingListResponse>>> groupPurchasePostRankingHandler(@RequestParam(required = false) RankingPeriod period) {

        // 검색 서비스 호출
        List<GroupPurchasePostRankingListResponse> groupPurchasePostRankingListResponse = rankingService.groupPurchasePostRanking(period);

        // 공통 응답 포맷으로 반환
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("공동구매 게시물 랭킹 조회 성공", groupPurchasePostRankingListResponse));
    }

    /**
     * 인기 검색어 랭킹 (클릭로그 or 키워드 조회수)
     */
    @GetMapping("/ranking/searchLog")
    public ResponseEntity<CommonResponse<List<SearchLogRankingListResponse>>> searchLogKeywordRankingHandler(@RequestParam(required = false) RankingPeriod period) {

        // 검색 서비스 호출
        List<SearchLogRankingListResponse> searchLogRankingListResponse = rankingService.searchLogKeywordRanking(period);

        // 공통 응답 포맷으로 반환
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("인기 검색어 랭킹 조회 성공", searchLogRankingListResponse));
    }

    /**
     * 실시간 공동구매 상품 게시물 랭킹(찜) (유저용 - 실시간)
     */
    @GetMapping("/ranking/favorite")
    public ResponseEntity<CommonResponse<List<ProductPostFavoriteRankingListResponse>>> productPostFavoriteRankingHandler(@RequestParam(required = false) RankingPeriod period) {

        // 검색 서비스 호출
        List<ProductPostFavoriteRankingListResponse> productPostFavoriteRankingListResponse = rankingService.loadRealtimeFavoriteRanking(period);

        // 공통 응답 포맷으로 반환
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("상품 판매 게시물 랭킹 조회 성공", productPostFavoriteRankingListResponse));
    }

    /**
     * 상품판매 게시물 랭킹 (찜 기준) - 관리자용
     */
    @GetMapping("/admin/ranking/favorite")
    public ResponseEntity<CommonResponse<List<ProductPostFavoriteRankingListResponse>>> productPostFavoriteRankingAdminHandler(@AuthenticationPrincipal AuthUser authUser, @RequestParam(required = false) RankingPeriod period, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        // 검색 서비스 호출
        List<ProductPostFavoriteRankingListResponse> productPostFavoriteRankingListResponse = rankingService.loadPeriodFavoriteRanking(authUser, period, from, to);

        // 공통 응답 포맷으로 반환
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("상품 판매 게시물 랭킹 조회 성공", productPostFavoriteRankingListResponse));
    }

}


