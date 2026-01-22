package com.zero9platform.domain.product_post_option.service;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post.repository.ProductPostRepository;
import com.zero9platform.domain.product_post_option.entity.ProductPostOption;
import com.zero9platform.domain.product_post_option.model.request.ProductPostOptionCreateRequest;
import com.zero9platform.domain.product_post_option.model.response.ProductPostOptionCreateResponse;
import com.zero9platform.domain.product_post_option.repository.ProductPostOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductPostOptionService {

    private final ProductPostOptionRepository optionRepository;
    private final ProductPostRepository productPostRepository;

    public ProductPostOptionCreateResponse optionCreate(Long productPostId, ProductPostOptionCreateRequest req) {

        // 판매게시물이 먼저라면 판매게시물이 존재하는지 검증 추가 예정
        ProductPost productPost = productPostRepository.findById(productPostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_POST_NOT_FOUND));

        ProductPostOption option = new ProductPostOption(
                productPost,
                req.getName(),
                req.getPrice(),
                req.getCapacity()
        );

        optionRepository.save(option);
        return ProductPostOptionCreateResponse.from(option);
    }
}

