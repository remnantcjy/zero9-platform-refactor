package com.zero9platform.domain.gpp_follow;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.domain.gpp_follow.model.response.GppFollowGetDetailResponse;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/zero9")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    /**
     * 공동구매 게시물 일정 팔로우
     */
    @PostMapping("/{gppId}/follows")
    public ResponseEntity<CommonResponse<Void>> gppFollowCreateHandler(@PathVariable Long gppId) {

        // 토큰 추가 예정 - Influencer, User

        Long userId = 1L;

        followService.gppFollowCreate(userId, gppId);   // 사용자가 공동구매 게시물을 구독

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("공동구매 게시물 일정 팔로우 성공", null));
    }

    /**
     * 공동구매 게시물 일정 팔로우 취소
     */
    @DeleteMapping("/{gppId}/follows")
    public ResponseEntity<CommonResponse<Void>> gppFollowDeleteHandler(@PathVariable Long gppId) {

        // 토큰 추가 예정

        Long userId = 1L;

        followService.gppFollowDelete(userId, gppId);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("공동구매 게시물 일정 팔로우 취소 성공", null));
    }

    /**
     * 공동구매 게시물 일정 팔로우 목록 조회 - 추후 Page 및 stream 변환 작업
     */
    @GetMapping("/{userId}/follows")
    public ResponseEntity<CommonResponse<Page<GppFollowGetDetailResponse>>> gppFollowGetListHandler(@PathVariable Long userId, @PageableDefault(page = 0, size = 10) Pageable pageable) {

        // 토큰 추가 예정

        Page<GppFollowGetDetailResponse> gppList = followService.gppFollowGetList(userId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("공동구매 게시물 일정 팔로우 목록 조회 성공", gppList));
    }
}
