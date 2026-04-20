package com.ecommerce.service.impl;

import com.ecommerce.dto.ProductDto;
import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.Role;
import com.ecommerce.entity.User;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              UserRepository userRepository,
                              CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Page<ProductDto> getProducts(String search, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        String term = search == null ? "" : search;
        return productRepository.findByApprovedTrueAndNameContainingIgnoreCase(term, pageable)
                .map(this::toDto);
    }

    @Override
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findByIdAndApprovedTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return toDto(product);
    }

    @Override
    public Page<ProductDto> getAdminProducts(String search, Boolean approved, int page, int size, String sortBy, String sortDir) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        String term = search == null ? "" : search;

        if (approved == null) {
            return productRepository.findByNameContainingIgnoreCase(term, pageable).map(this::toDto);
        }

        return productRepository.findByApprovedAndNameContainingIgnoreCase(approved, term, pageable)
                .map(this::toDto);
    }

    @Override
    public Page<ProductDto> getVendorProducts(String email, int page, int size, String sortBy) {
        User vendor = getVendorByEmail(email);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return productRepository.findByVendor(vendor, pageable).map(this::toDto);
    }

    @Override
    @Transactional
    public ProductDto createVendorProduct(String email, ProductDto request) {
        User vendor = getVendorByEmail(email);
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .approved(false)
                .vendor(vendor)
                .category(resolveCategory(request.getCategoryName()))
                .build();

        return toDto(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductDto updateVendorProduct(String email, Long id, ProductDto request) {
        User vendor = getVendorByEmail(email);
        Product product = productRepository.findByIdAndVendor(id, vendor)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setApproved(false);
        product.setCategory(resolveCategory(request.getCategoryName()));

        return toDto(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteVendorProduct(String email, Long id) {
        User vendor = getVendorByEmail(email);
        Product product = productRepository.findByIdAndVendor(id, vendor)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        productRepository.delete(product);
    }

    @Override
    @Transactional
    public ProductDto approveProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setApproved(true);
        return toDto(productRepository.save(product));
    }

    private User getVendorByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getRole() != Role.VENDOR) {
            throw new BadRequestException("User is not a vendor");
        }
        return user;
    }

    private Category resolveCategory(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) {
            return null;
        }

        return categoryRepository.findByNameIgnoreCase(categoryName.trim())
                .orElseGet(() -> categoryRepository.save(Category.builder().name(categoryName.trim()).build()));
    }

    private ProductDto toDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .approved(product.isApproved())
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .build();
    }
}
