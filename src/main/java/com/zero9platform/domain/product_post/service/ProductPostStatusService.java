package com.zero9platform.domain.product_post.service;

import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post.repository.ProductPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductPostStatusService {

    private final ProductPostRepository productPostRepository;

    @Transactional
    public void updateProgressStatusIndividually(LocalDateTime now) {
        List<ProductPost> productPostList = productPostRepository.findAll();
        for (ProductPost productPost: productPostList) {
            productPost.updateProductProgressStatus(now);
        }
    }
}