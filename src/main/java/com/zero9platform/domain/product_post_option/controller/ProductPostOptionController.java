package com.zero9platform.domain.product_post_option.controller;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.domain.product_post_option.model.request.ProductPostOptionCreateRequest;
import com.zero9platform.domain.product_post_option.model.response.ProductPostOptionCreateResponse;
import com.zero9platform.domain.product_post_option.service.ProductPostOptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/zero9/product-posts")
public class ProductPostOptionController {

    private final ProductPostOptionService postOptionService;

    @PostMapping("/{productPostId}/options")
    public ResponseEntity<CommonResponse<ProductPostOptionCreateResponse>> optionCreateHandler(@PathVariable Long productPostId, @RequestBody @Valid ProductPostOptionCreateRequest request) {

        ProductPostOptionCreateResponse response = postOptionService.optionCreate(productPostId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success("옵션 생성 성공", response));
    }
}
