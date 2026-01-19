package com.zero9platform.domain.admin.model.request.gp_post;

import com.zero9platform.common.enums.GppApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GpPostApproveRequest {

    private GppApprovalStatus status;
}
