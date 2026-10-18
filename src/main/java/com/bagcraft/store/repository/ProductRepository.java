package com.bagcraft.store.repository;

import com.bagcraft.store.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryIdAndActiveTrue(Long categoryId);
    List<Product> findByActiveTrue();
    List<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name);
}
