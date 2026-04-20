package com.ecommerce.repository;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByCustomer(User customer, Pageable pageable);

    @Query("""
            select distinct o
            from Order o
            join o.items i
            where i.product.vendor.id = :vendorId
            """)
    Page<Order> findOrdersByVendorId(@Param("vendorId") Long vendorId, Pageable pageable);
}
