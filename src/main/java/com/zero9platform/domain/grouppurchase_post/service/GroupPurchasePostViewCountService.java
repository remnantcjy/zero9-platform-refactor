package com.zero9platform.domain.grouppurchase_post.service;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.domain.grouppurchase_post.repository.GroupPurchasePostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupPurchasePostViewCountService {

    private final GroupPurchasePostRepository groupPurchasePostRepository;

    @Transactional
    public void increaseViewCount(Long gppId) {
        int updated = groupPurchasePostRepository.increaseViewCount(gppId);
        if (updated == 0) {
            throw new CustomException(ExceptionCode.GPP_NOT_FOUND);
        }
    }

}
