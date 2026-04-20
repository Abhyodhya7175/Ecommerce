package com.ecommerce.service.impl;

import com.ecommerce.dto.CartDto;
import com.ecommerce.entity.Cart;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.Role;
import com.ecommerce.entity.User;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    public CartServiceImpl(CartRepository cartRepository,
                           UserRepository userRepository,
                           ProductRepository productRepository,
                           CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    public CartDto.CartResponse getCart(String email) {
        Cart cart = getOrCreateCart(email);
        return toCartResponse(cart);
    }

    @Override
    @Transactional
    public CartDto.CartResponse addToCart(String email, CartDto.AddToCartRequest request) {
        Cart cart = getOrCreateCart(email);
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (!product.isApproved()) {
            throw new BadRequestException("Product is not approved");
        }
        if (product.getStock() < request.getQuantity()) {
            throw new BadRequestException("Requested quantity exceeds stock");
        }

        CartItem existingItem = cart.getItems().stream()
                .filter(ci -> ci.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            int newQty = existingItem.getQuantity() + request.getQuantity();
            if (product.getStock() < newQty) {
                throw new BadRequestException("Requested quantity exceeds stock");
            }
            existingItem.setQuantity(newQty);
        } else {
            CartItem item = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cart.getItems().add(item);
        }
        cartRepository.save(cart);
        return toCartResponse(cart);
    }

    @Override
    @Transactional
    public void removeItem(String email, Long itemId) {
        Cart cart = getOrCreateCart(email);
        CartItem item = cart.getItems().stream()
                .filter(ci -> ci.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        cart.getItems().remove(item);
        cartRepository.save(cart);
        cartItemRepository.delete(item);
    }

    private Cart getOrCreateCart(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getRole() != Role.CUSTOMER) {
            throw new BadRequestException("Only customers can access cart");
        }

        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart cart = Cart.builder()
                    .user(user)
                    .items(new ArrayList<>())
                    .build();
            return cartRepository.save(cart);
        });
    }

    private CartDto.CartResponse toCartResponse(Cart cart) {
        return CartDto.CartResponse.builder()
                .cartId(cart.getId())
                .items(cart.getItems().stream().map(item -> CartDto.CartItemResponse.builder()
                        .itemId(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .price(item.getProduct().getPrice())
                        .build()).toList())
                .build();
    }
}
