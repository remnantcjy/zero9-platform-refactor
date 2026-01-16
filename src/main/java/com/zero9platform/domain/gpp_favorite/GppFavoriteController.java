package com.zero9platform.domain.gpp_favorite;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.domain.gpp_favorite.entity.GppFavorite;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/zero9")
public class GppFavoriteController {

    private final GppFavoriteService gppFavoriteService;

    //찜 등록
    @PostMapping("/{gppId}/favorites")
    public ResponseEntity<CommonResponse<Void>> favoriteCreateHandler(
            @PathVariable Long gppId,
            @RequestBody Long userId) {

        if (gppId == null || gppId <= 0) {
            throw new CustomException(ExceptionCode.INVALID_GPP_ID);
        }

        if(userId == null || userId <= 0) {
            throw new CustomException(ExceptionCode.INVALID_USER_ID);
        }

        gppFavoriteService.favoriteCreate(gppId,userId);
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse<>(true, "찜 등록 성공", null));
    }

    //찜 목록 조회
    @GetMapping("/favorites")
    public ResponseEntity<CommonResponse<List<GppFavorite>>> favoriteGetPageHandler() {
    return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse<>(true, "찜 목록 조회 성공", null));
    }

    //찜 취소
}

