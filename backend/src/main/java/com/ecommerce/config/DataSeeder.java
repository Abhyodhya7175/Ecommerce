package com.ecommerce.config;

import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.Role;
import com.ecommerce.entity.User;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      CategoryRepository categoryRepository,
                      ProductRepository productRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        User customer = createUserIfMissing("Demo Customer", "customer@example.com", "demo123", Role.CUSTOMER);
        User vendor = createUserIfMissing("Demo Vendor", "vendor@example.com", "demo123", Role.VENDOR);
        createUserIfMissing("Demo Admin", "admin@example.com", "demo123", Role.ADMIN);

        Category electronics = createCategoryIfMissing("Electronics");
        Category home = createCategoryIfMissing("Home");
        Category fashion = createCategoryIfMissing("Fashion");

        createProductIfMissing(
                "Noise-Cancelling Headphones",
                "Wireless over-ear headphones with active noise cancellation.",
                new BigDecimal("129.99"),
                25,
                vendor,
                electronics
        );

        createProductIfMissing(
                "Smart Fitness Watch",
                "Track heart rate, sleep, and activity with 7-day battery life.",
                new BigDecimal("89.50"),
                30,
                vendor,
                electronics
        );

        createProductIfMissing(
                "Minimal Desk Lamp",
                "Adjustable warm/cool light desk lamp for work and study.",
                new BigDecimal("39.90"),
                42,
                vendor,
                home
        );

        createProductIfMissing(
                "Ceramic Coffee Mug Set",
                "Set of 4 handcrafted ceramic mugs.",
                new BigDecimal("24.00"),
                55,
                vendor,
                home
        );

        createProductIfMissing(
                "Classic Cotton Hoodie",
                "Soft everyday hoodie with regular fit.",
                new BigDecimal("44.75"),
                38,
                vendor,
                fashion
        );

        createProductIfMissing(
                "Urban Running Shoes",
                "Lightweight running shoes with breathable mesh upper.",
                new BigDecimal("69.95"),
                28,
                vendor,
                fashion
        );

        // Ensure customer cart/order paths work with at least one visible approved product set.
        if (customer == null) {
            throw new IllegalStateException("Demo customer should be present after seeding");
        }
    }

    private User createUserIfMissing(String name, String email, String rawPassword, Role role) {
        return userRepository.findByEmail(email).orElseGet(() -> userRepository.save(
                User.builder()
                        .name(name)
                        .email(email)
                        .password(passwordEncoder.encode(rawPassword))
                        .role(role)
                        .build()
        ));
    }

    private Category createCategoryIfMissing(String name) {
        return categoryRepository.findByNameIgnoreCase(name).orElseGet(() -> categoryRepository.save(
                Category.builder().name(name).build()
        ));
    }

    private void createProductIfMissing(String name,
                                        String description,
                                        BigDecimal price,
                                        int stock,
                                        User vendor,
                                        Category category) {
        boolean exists = productRepository.findAll().stream().anyMatch(product ->
                product.getVendor().getId().equals(vendor.getId())
                        && product.getName().equalsIgnoreCase(name)
        );

        if (exists) {
            return;
        }

        productRepository.save(Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .stock(stock)
                .approved(true)
                .vendor(vendor)
                .category(category)
                .build());
    }
}
