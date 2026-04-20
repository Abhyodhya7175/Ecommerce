package com.ecommerce.controller;

import com.ecommerce.dto.ProductDto;
import com.ecommerce.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vendor")
public class VendorController {

    private final ProductService productService;

    public VendorController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public ResponseEntity<Page<ProductDto>> getVendorProducts(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        return ResponseEntity.ok(productService.getVendorProducts(authentication.getName(), page, size, sortBy));
    }

    @PostMapping("/products")
    public ResponseEntity<ProductDto> createProduct(
            Authentication authentication,
            @RequestBody ProductDto request
    ) {
        ProductDto created = productService.createVendorProduct(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody ProductDto request
    ) {
        return ResponseEntity.ok(productService.updateVendorProduct(authentication.getName(), id, request));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(Authentication authentication, @PathVariable Long id) {
        productService.deleteVendorProduct(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
