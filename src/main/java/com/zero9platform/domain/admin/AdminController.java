package com.zero9platform.domain.admin;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.domain.admin.model.request.gp_post.GpPostApproveRequest;
import com.zero9platform.domain.admin.model.request.influencer.InfluencerApproveRequest;
import com.zero9platform.domain.admin.model.response.gp_post.GpPostApproveResponse;
import com.zero9platform.domain.admin.model.response.influencer.InfluencerApproveResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/zero9/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * 인플루언서 가입 승인
     */
    @PutMapping("/influencers/{userId}/approval")
    public ResponseEntity<CommonResponse<InfluencerApproveResponse>> influencerApproveHandler(@PathVariable Long userId, @Valid @RequestBody InfluencerApproveRequest request) {

        InfluencerApproveResponse response = adminService.influencerApprove(userId, request);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("인플루언서 가입 승인 성공", response));
    }

    /**
     * 인플루언서 공동구매 게시물 승인
     */
    @PutMapping("/gp-posts/{gppId}/approval")
    public ResponseEntity<CommonResponse<GpPostApproveResponse>> gpPostApproveHandler(@PathVariable Long gppId, @RequestBody GpPostApproveRequest request) {

        GpPostApproveResponse response = adminService.gpPostApprove(gppId, request);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("인플루언서 공동구매 게시물 승인 성공", response));
    }
}