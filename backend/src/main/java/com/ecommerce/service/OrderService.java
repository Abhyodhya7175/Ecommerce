package com.ecommerce.service;

import com.ecommerce.dto.OrderDto;
import org.springframework.data.domain.Page;

public interface OrderService {
    OrderDto placeOrder(String email);
    Page<OrderDto> getCustomerOrders(String email, int page, int size, String sortBy);
    Page<OrderDto> getVendorOrders(String email, int page, int size, String sortBy);
    Page<OrderDto> getAdminOrders(int page, int size, String sortBy);
}
