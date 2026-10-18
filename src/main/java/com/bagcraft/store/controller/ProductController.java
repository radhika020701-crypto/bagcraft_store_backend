package com.bagcraft.store.controller;

import com.bagcraft.store.exception.NotFoundException;
import com.bagcraft.store.model.Product;
import com.bagcraft.store.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;

    @GetMapping
    public List<Product> getAll(@RequestParam(required = false) Long categoryId,
                                 @RequestParam(required = false) String search) {
        if (search != null && !search.isBlank()) {
            return productRepository.findByNameContainingIgnoreCaseAndActiveTrue(search);
        }
        if (categoryId != null) {
            return productRepository.findByCategoryIdAndActiveTrue(categoryId);
        }
        return productRepository.findByActiveTrue();
    }

    @GetMapping("/{id}")
    public Product getOne(@PathVariable Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found: " + id));
    }
}
