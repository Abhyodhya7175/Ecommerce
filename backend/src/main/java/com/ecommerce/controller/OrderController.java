package com.ecommerce.controller;

import com.ecommerce.dto.OrderDto;
import com.ecommerce.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderDto> placeOrder(Authentication authentication) {
        return ResponseEntity.ok(orderService.placeOrder(authentication.getName()));
    }

    @GetMapping
    public ResponseEntity<Page<OrderDto>> getOrders(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy
    ) {
        return ResponseEntity.ok(orderService.getCustomerOrders(authentication.getName(), page, size, sortBy));
    }

    @GetMapping("/vendor")
    public ResponseEntity<Page<OrderDto>> getVendorOrders(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy
    ) {
        return ResponseEntity.ok(orderService.getVendorOrders(authentication.getName(), page, size, sortBy));
    }
}
