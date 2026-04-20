package com.ecommerce.service.impl;

import com.ecommerce.dto.OrderDto;
import com.ecommerce.entity.Cart;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;
import com.ecommerce.entity.Role;
import com.ecommerce.entity.User;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
        private final ProductRepository productRepository;

    public OrderServiceImpl(UserRepository userRepository,
                            CartRepository cartRepository,
                                                        OrderRepository orderRepository,
                                                        ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
                this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public OrderDto placeOrder(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                if (user.getRole() != Role.CUSTOMER) {
                        throw new BadRequestException("Only customers can place orders");
                }

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

                cart.getItems().forEach(ci -> {
                        if (!ci.getProduct().isApproved()) {
                                throw new BadRequestException("Cart contains unapproved product");
                        }
                        if (ci.getProduct().getStock() < ci.getQuantity()) {
                                throw new BadRequestException("Insufficient stock for product: " + ci.getProduct().getName());
                        }
                });

        Order order = Order.builder()
                .customer(user)
                .createdAt(LocalDateTime.now())
                .build();

        order.setItems(cart.getItems().stream().map(ci -> OrderItem.builder()
                .order(order)
                .product(ci.getProduct())
                .quantity(ci.getQuantity())
                .price(ci.getProduct().getPrice())
                .build()).toList());

        cart.getItems().forEach(ci -> {
            ci.getProduct().setStock(ci.getProduct().getStock() - ci.getQuantity());
            productRepository.save(ci.getProduct());
        });

        Order saved = orderRepository.save(order);
        cart.getItems().clear();
        cartRepository.save(cart);
        return toDto(saved);
    }

    @Override
    public Page<OrderDto> getCustomerOrders(String email, int page, int size, String sortBy) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                if (user.getRole() != Role.CUSTOMER) {
                        throw new BadRequestException("Only customers can view customer orders");
                }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        return orderRepository.findByCustomer(user, pageable).map(this::toDto);
    }

        @Override
        public Page<OrderDto> getVendorOrders(String email, int page, int size, String sortBy) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                if (user.getRole() != Role.VENDOR) {
                        throw new BadRequestException("Only vendors can view vendor orders");
                }

                Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
                return orderRepository.findOrdersByVendorId(user.getId(), pageable)
                                .map(order -> toVendorDto(order, user.getId()));
        }

        @Override
        public Page<OrderDto> getAdminOrders(int page, int size, String sortBy) {
                Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
                return orderRepository.findAll(pageable).map(this::toDto);
        }

        private OrderDto toVendorDto(Order order, Long vendorId) {
                List<OrderDto.OrderItemDto> vendorItems = order.getItems().stream()
                                .filter(item -> item.getProduct().getVendor().getId().equals(vendorId))
                                .map(item -> OrderDto.OrderItemDto.builder()
                                                .productId(item.getProduct().getId())
                                                .productName(item.getProduct().getName())
                                                .quantity(item.getQuantity())
                                                .price(item.getPrice())
                                                .build())
                                .toList();

                return OrderDto.builder()
                                .id(order.getId())
                                .createdAt(order.getCreatedAt())
                                .items(vendorItems)
                                .build();
        }

    private OrderDto toDto(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .createdAt(order.getCreatedAt())
                .items(order.getItems().stream().map(item -> OrderDto.OrderItemDto.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build()).toList())
                .build();
    }
}
