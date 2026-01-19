package com.zero9platform.domain.admin;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.domain.admin.model.request.gp_post.GpPostApproveRequest;
import com.zero9platform.domain.admin.model.response.gp_post.GpPostApproveResponse;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.grouppurchase_post.repository.GroupPurchasePostRepository;
import com.zero9platform.domain.admin.repository.InfluencerRepository;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.repository.UserRepository;
import com.zero9platform.domain.admin.entity.Influencer;
import com.zero9platform.domain.admin.model.request.influencer.InfluencerApproveRequest;
import com.zero9platform.domain.admin.model.response.influencer.InfluencerApproveResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final InfluencerRepository influencerRepository;
    private final GroupPurchasePostRepository groupPurchasePostRepository;
    private final UserRepository userRepository;

    /**
     * 인플루언서 가입 승인
     */
    @Transactional
    public InfluencerApproveResponse influencerApprove(Long userId, InfluencerApproveRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        Influencer influencer = influencerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_IS_NOT_INFLUENCER));

        // 인플루언서 승인
        influencer.influencerApprove(request.getApprove());

        return InfluencerApproveResponse.from(influencer);
    }

    /**
     * 인플루언서 공동구매 게시물 승인
     */
    @Transactional
    public GpPostApproveResponse gpPostApprove(Long gppId, GpPostApproveRequest request) {

        GroupPurchasePost gpp = groupPurchasePostRepository.findById(gppId)
                .orElseThrow(() -> new CustomException(ExceptionCode.GPP_NOT_FOUND));

        // 공동게시물 상태 변경
        gpp.GppApprove(request.getStatus());

        return new GpPostApproveResponse(gppId, request.getStatus().name());
    }

}
