package com.bagcraft.store.controller;

import com.bagcraft.store.dto.ProductRequest;
import com.bagcraft.store.exception.NotFoundException;
import com.bagcraft.store.model.*;
import com.bagcraft.store.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// All endpoints here require ROLE_ADMIN (enforced in SecurityConfig via /api/admin/**)
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;

    // ---- Products ----
    @PostMapping("/products")
    public Product createProduct(@Valid @RequestBody ProductRequest req) {
        Category category = req.getCategoryId() != null
                ? categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"))
                : null;

        Product product = Product.builder()
                .name(req.getName())
                .description(req.getDescription())
                .price(req.getPrice())
                .stockQuantity(req.getStockQuantity())
                .imageUrl(req.getImageUrl())
                .material(req.getMaterial())
                .category(category)
                .active(true)
                .build();
        return productRepository.save(product);
    }

    @PutMapping("/products/{id}")
    public Product updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest req) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        product.setName(req.getName());
        product.setDescription(req.getDescription());
        product.setPrice(req.getPrice());
        product.setStockQuantity(req.getStockQuantity());
        product.setImageUrl(req.getImageUrl());
        product.setMaterial(req.getMaterial());
        if (req.getCategoryId() != null) {
            product.setCategory(categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found")));
        }
        return productRepository.save(product);
    }

    @DeleteMapping("/products/{id}")
    public void deactivateProduct(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        product.setActive(false); // soft delete
        productRepository.save(product);
    }

    // ---- Categories ----
    @PostMapping("/categories")
    public Category createCategory(@RequestBody Category category) {
        return categoryRepository.save(category);
    }

    // ---- Orders ----
    @GetMapping("/orders")
    public List<Order> allOrders() {
        return orderRepository.findAll();
    }

    @PutMapping("/orders/{id}/status")
    public Order updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
