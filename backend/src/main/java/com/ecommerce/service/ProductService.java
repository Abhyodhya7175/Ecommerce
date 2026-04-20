package com.ecommerce.service;

import com.ecommerce.dto.ProductDto;
import org.springframework.data.domain.Page;

public interface ProductService {
    Page<ProductDto> getProducts(String search, int page, int size, String sortBy);
    ProductDto getProductById(Long id);
    Page<ProductDto> getVendorProducts(String email, int page, int size, String sortBy);
    ProductDto createVendorProduct(String email, ProductDto request);
    ProductDto updateVendorProduct(String email, Long id, ProductDto request);
    void deleteVendorProduct(String email, Long id);
    ProductDto approveProduct(Long id);
}
