
package com.fittrack_app.fittrack.modules.producte.repository;

import com.fittrack_app.fittrack.modules.producte.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository
        extends JpaRepository<Product, Long> {

    @Query("""
            SELECT p FROM Product p
            LEFT JOIN p.owner o
            WHERE p.owner IS NULL OR o.email = :email
            """)
    Page<Product> findVisibleProducts(
            @Param("email") String email,
            Pageable pageable
    );

    @Query("""
            SELECT p FROM Product p
            LEFT JOIN p.owner o
            WHERE (p.owner IS NULL OR o.email = :email)
              AND (
                LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(COALESCE(p.brand, ''))
                   LIKE LOWER(CONCAT('%', :query, '%'))
              )
            """)
    Page<Product> searchVisibleProducts(
            @Param("email") String email,
            @Param("query") String query,
            Pageable pageable
    );

    Optional<Product> findByBarcode(String barcode);

    boolean existsByBarcode(String barcode);
}
