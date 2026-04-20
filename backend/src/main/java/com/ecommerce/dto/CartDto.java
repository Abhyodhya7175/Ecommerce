package com.ecommerce.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

public class CartDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AddToCartRequest {
        private Long productId;
        private Integer quantity;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CartItemResponse {
        private Long itemId;
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal price;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CartResponse {
        private Long cartId;
        private List<CartItemResponse> items;
    }
}
