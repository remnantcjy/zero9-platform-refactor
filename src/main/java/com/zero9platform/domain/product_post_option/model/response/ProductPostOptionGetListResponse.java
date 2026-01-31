//package com.zero9platform.domain.product_post_option.model.response;
//
//import com.zero9platform.common.enums.StockStatus;
//import com.zero9platform.domain.product_post_option.entity.ProductPostOption;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//
//import java.time.LocalDateTime;
//
//@Getter
//@AllArgsConstructor
//public class ProductPostOptionGetListResponse {
//
//    private final Long id;
//    private final String name;
//    private final Long optionPrice;
//    private final Integer capacity;
//    private final String optionStatus;
//    private final LocalDateTime createdAt;
//    private final LocalDateTime updatedAt;
//
//    public static ProductPostOptionGetListResponse from(ProductPostOption option) {
//
//        StockStatus stockStatus = StockStatus.valueOf(option.getOptionStatus());
//
//        return new ProductPostOptionGetListResponse(
//                option.getId(),
//                option.getName(),
//                option.getOptionPrice(),
//                option.getCapacity(),
//                stockStatus.name(),
//                option.getCreatedAt(),
//                option.getUpdatedAt()
//        );
//    }
//}