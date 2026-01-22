package com.zero9platform.domain.product_post.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProductPostCreateRequest {

    private String title;
    private String content;
    private Long stock;
    private String image;
    private String category;
    private String productPostProgressStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
