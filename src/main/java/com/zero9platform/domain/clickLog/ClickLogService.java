package com.zero9platform.domain.clickLog;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post.repository.ProductPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClickLogService {

    private final ClickLogRepository clickLogRepository;
    private final ProductPostRepository productPostRepository;
    private final SearchContextRepository searchContextRepository;


    @Transactional
    public ClickLogProductPostDetailResponse productPostDetail(Long productPostId, String ignoredKeyword) {

        ProductPost productPost = productPostRepository.findByIdAndDeletedAtIsNull(productPostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_POST));

        searchContextRepository.findTopByProductPostIdOrderByCreatedAtDesc(productPostId).ifPresent(ctx ->
                        clickLogRepository.save(
                                new ClickLog(productPost, ctx.getKeyword())
                        )
                );
        return ClickLogProductPostDetailResponse.from(productPost);
    }
}
