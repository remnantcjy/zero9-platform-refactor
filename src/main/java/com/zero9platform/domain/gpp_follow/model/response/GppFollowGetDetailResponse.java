package com.zero9platform.domain.gpp_follow.model.response;

import com.zero9platform.domain.gpp_follow.entity.GppFollow;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class GppFollowGetDetailResponse {

    private final Long gppId;
    private final String nickname;
    private final String productName;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public static GppFollowGetDetailResponse from(GroupPurchasePost gpp) {
        return new GppFollowGetDetailResponse(
                gpp.getId(),
                gpp.getUser().getNickname(),
                gpp.getProductName(),
                gpp.getStartDate(),
                gpp.getEndDate()
        );
    }

    public static Page<GppFollowGetDetailResponse> from(Page<GroupPurchasePost> gppList) {
        return gppList.map(GppFollowGetDetailResponse::from);
    }
}
