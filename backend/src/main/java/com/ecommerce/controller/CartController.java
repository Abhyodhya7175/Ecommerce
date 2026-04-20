package com.ecommerce.controller;

import com.ecommerce.dto.CartDto;
import com.ecommerce.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartDto.CartResponse> getCart(Authentication authentication) {
        return ResponseEntity.ok(cartService.getCart(authentication.getName()));
    }

    @PostMapping("/add")
    public ResponseEntity<CartDto.CartResponse> addToCart(
            Authentication authentication,
            @RequestBody CartDto.AddToCartRequest request
    ) {
        return ResponseEntity.ok(cartService.addToCart(authentication.getName(), request));
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Void> removeItem(Authentication authentication, @PathVariable Long id) {
        cartService.removeItem(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
