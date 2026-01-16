package com.zero9platform.domain.gpp_favorite;

import com.zero9platform.common.model.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        gppFavoriteService.favoriteCreate(gppId,userId);
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse<>(true, "찜 등록 성공", null));
    }

    //찜 목록 조회

    //찜 취소
}

