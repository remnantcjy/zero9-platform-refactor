package com.zero9platform.domain.product_post_option.service;


import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post.repository.ProductPostRepository;
import com.zero9platform.domain.product_post_option.entity.ProductPostOption;
import com.zero9platform.domain.product_post_option.model.request.ProductPostOptionCreateRequest;
import com.zero9platform.domain.product_post_option.model.request.ProductPostOptionUpdateRequest;
import com.zero9platform.domain.product_post_option.model.response.ProductPostOptionCreateResponse;
import com.zero9platform.domain.product_post_option.model.response.ProductPostOptionGetDetailResponse;
import com.zero9platform.domain.product_post_option.model.response.ProductPostOptionGetListResponse;
import com.zero9platform.domain.product_post_option.model.response.ProductPostOptionUpdateResponse;
import com.zero9platform.domain.product_post_option.repository.ProductPostOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductPostOptionService {

    private final ProductPostOptionRepository optionRepository;
    private final ProductPostRepository productPostRepository;

    @Transactional
    public ProductPostOptionCreateResponse optionCreate(Long productPostId, ProductPostOptionCreateRequest req) {
        // 판매게시물이 먼저라면 판매게시물이 존재하는지 검증 추가 예정
        ProductPost productPost = productPostRepository.findById(productPostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_POST_NOT_FOUND));


        ProductPostOption option = new ProductPostOption(productPost, req.getName(), req.getPrice(), req.getCapacity());

        optionRepository.save(option);
        return ProductPostOptionCreateResponse.from(option);
    }

    @Transactional(readOnly = true)
    public ProductPostOptionGetDetailResponse optionGetDetail(Long productPostId, Long optionId) {

        productPostRepository.findByIdAndDeletedAtIsNull(productPostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_POST_NOT_FOUND));

        ProductPostOption option = optionRepository.findByIdAndProductPost_Id(optionId, productPostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_POST_OPTION_NOT_FOUND));

        return ProductPostOptionGetDetailResponse.from(option);
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductPostOptionGetListResponse> optionGetPage(Long productPostId, Pageable pageable) {

        productPostRepository.findByIdAndDeletedAtIsNull(productPostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_POST_NOT_FOUND));

        Page<ProductPostOptionGetListResponse> page = optionRepository
                .findAllByProductPost_Id(productPostId, pageable)
                .map(ProductPostOptionGetListResponse::from);

        return PageResponse.from(page);
    }

    @Transactional
    public ProductPostOptionUpdateResponse optionUpdate(Long productPostId, Long optionId, ProductPostOptionUpdateRequest request) {

        ProductPostOption option = optionRepository.findByIdAndProductPost_Id(optionId, productPostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_POST_OPTION_NOT_FOUND));

        option.update(request.getName(), request.getPrice(), request.getCapacity());

        return ProductPostOptionUpdateResponse.from(option);
    }

}

