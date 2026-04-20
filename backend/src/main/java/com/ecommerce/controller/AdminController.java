package com.ecommerce.controller;

import com.ecommerce.dto.OrderDto;
import com.ecommerce.dto.ProductDto;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;
    private final OrderService orderService;

    public AdminController(ProductService productService, OrderService orderService) {
        this.productService = productService;
        this.orderService = orderService;
    }

    @PutMapping("/products/{id}/approve")
    public ResponseEntity<ProductDto> approveProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.approveProduct(id));
    }

    @GetMapping("/products")
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) Boolean approved,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        return ResponseEntity.ok(productService.getAdminProducts(search, approved, page, size, sortBy, sortDir));
    }

    @GetMapping("/orders")
    public ResponseEntity<Page<OrderDto>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy
    ) {
        return ResponseEntity.ok(orderService.getAdminOrders(page, size, sortBy));
    }
}
