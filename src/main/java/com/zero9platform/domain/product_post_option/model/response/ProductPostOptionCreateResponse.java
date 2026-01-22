package com.zero9platform.domain.product_post_option.model.response;

import com.zero9platform.domain.product_post_option.entity.ProductPostOption;
import lombok.Getter;

@Getter
public class ProductPostOptionCreateResponse {

    private final Long id;
    private final String name;
    private final Long price;
    private final Integer capacity;

    private ProductPostOptionCreateResponse(Long id, String name, Long price, Integer capacity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.capacity = capacity;
    }

    public static ProductPostOptionCreateResponse from(ProductPostOption option) {
        return new ProductPostOptionCreateResponse(
                option.getId(),
                option.getName(),
                option.getPrice(),
                option.getCapacity()
        );
    }
}
