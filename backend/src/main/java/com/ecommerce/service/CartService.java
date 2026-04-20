package com.ecommerce.service;

import com.ecommerce.dto.CartDto;

public interface CartService {
    CartDto.CartResponse getCart(String email);
    CartDto.CartResponse addToCart(String email, CartDto.AddToCartRequest request);
    void removeItem(String email, Long itemId);
}
