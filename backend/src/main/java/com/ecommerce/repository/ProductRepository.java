package com.ecommerce.repository;

import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByApprovedTrueAndNameContainingIgnoreCase(String search, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCase(String search, Pageable pageable);
    Page<Product> findByApprovedAndNameContainingIgnoreCase(boolean approved, String search, Pageable pageable);
    Page<Product> findByVendor(User vendor, Pageable pageable);
    Optional<Product> findByIdAndApprovedTrue(Long id);
    Optional<Product> findByIdAndVendor(Long id, User vendor);
}
