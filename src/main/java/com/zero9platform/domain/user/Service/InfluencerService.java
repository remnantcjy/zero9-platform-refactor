package com.zero9platform.domain.user.Service;

import com.zero9platform.domain.admin.entity.Influencer;
import com.zero9platform.domain.user.model.influencer.InfluencerDetailResponse;
import com.zero9platform.domain.admin.repository.InfluencerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InfluencerService {

    private final InfluencerRepository influencerRepository;

    /**
     * 인플루언서 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<InfluencerDetailResponse> influencerList(Boolean status, Pageable pageable) {

        return influencerRepository.findByApprovalStatusAndUser(status, pageable)
                .map(InfluencerDetailResponse::from);

    }
}
