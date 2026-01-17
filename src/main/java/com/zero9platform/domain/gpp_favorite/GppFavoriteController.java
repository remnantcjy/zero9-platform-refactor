package com.zero9platform.domain.gpp_favorite;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.gpp_favorite.model.GppFavoriteCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/zero9")
public class GppFavoriteController {

    private final GppFavoriteService gppFavoriteService;

    /**
     * 찜 등록
     */
    @PostMapping("/{gppId}/favorites")
    public ResponseEntity<CommonResponse<GppFavoriteCreateResponse>> favoriteCreateHandler(@PathVariable Long gppId,
//                                                                                           @AuthenticationPrincipal AuthUser authUser,
                                                                                           @RequestBody Long userId) {

        if (gppId == null || gppId <= 0) {
            throw new CustomException(ExceptionCode.INVALID_GPP_ID);
        }

        if (userId == null || userId <= 0) {
            throw new CustomException(ExceptionCode.INVALID_USER_ID);
        }

        //        Long userId = authUser.getId(); // 토큰ID

        GppFavoriteCreateResponse createResponse = gppFavoriteService.favoriteCreate(gppId, userId);
        CommonResponse<GppFavoriteCreateResponse> commonResponse = new CommonResponse<>(true, "찜 등록 성공", createResponse);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    /**
     * 찜 목록 조회
     */
    @GetMapping("/favorites")
    public ResponseEntity<CommonResponse<PageResponse<GppFavoriteGetDto>>> favoriteListGetPageHandler(
//            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (userId == null || userId <= 0) {
            throw new CustomException(ExceptionCode.INVALID_USER_ID);
        }

//        Long userId = authUser.getId(); // 토큰ID

        PageResponse<GppFavoriteGetDto> favoriteList = gppFavoriteService.favoriteList(userId, page, size);

        CommonResponse<PageResponse<GppFavoriteGetDto>> commonResponse = new CommonResponse<>(true, "찜 목록 조회 성공", favoriteList);

        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    /**
     * 찜 등록 취소
     */
    @DeleteMapping("/{gppId}/favorites")
    public ResponseEntity<CommonResponse<Void>> favoriteCancellationHandler(
            @PathVariable Long gppId,
//          @AuthenticationPrincipal AuthUser authUser,
            @RequestBody Long userId) {

        if (gppId == null || gppId <= 0) {
            throw new CustomException(ExceptionCode.INVALID_GPP_ID);
        }

        gppFavoriteService.favoriteCancellation(gppId,userId);

        CommonResponse<Void> commonResponse = new CommonResponse<>(true, "찜 등록취소 성공", null);

        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

}

