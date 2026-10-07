package com.fittrack_app.fittrack.modules.producte.repository;

import com.fittrack_app.fittrack.modules.producte.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
