package com.zero9platform.domain.user.Controller;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.domain.user.Service.InfluencerService;
import com.zero9platform.domain.user.model.influencer.InfluencerDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/zero9")
@RequiredArgsConstructor
public class InfluencerController {

    private final InfluencerService influencerService;

    /**
     * 인플루언서 목록 조회
     */
    @GetMapping("/influencers")
    public ResponseEntity<CommonResponse<List<InfluencerDetailResponse>>> UserInfluencersHandler(@RequestParam(required = false) Boolean approved)  {

        List<InfluencerDetailResponse> influencerList = influencerService.influencerList(approved);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("인플루언서 목록 조회가 완료 되었습니다.", influencerList));
    }
}
