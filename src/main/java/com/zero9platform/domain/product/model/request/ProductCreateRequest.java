package com.zero9platform.domain.product.model.request;

import lombok.Getter;

@Getter
public class ProductCreateRequest {

    private String name;
    private String description;
    private Long price;
}
