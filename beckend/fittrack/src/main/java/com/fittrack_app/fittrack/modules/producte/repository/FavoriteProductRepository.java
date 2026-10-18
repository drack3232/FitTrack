package com.fittrack_app.fittrack.modules.producte.repository;

import com.fittrack_app.fittrack.modules.producte.entity.FavoriteProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteProductRepository
        extends JpaRepository<FavoriteProduct, Long> {

    boolean existsByUser_IdAndProduct_Id (Long id, Long productId);

    Page<FavoriteProduct> findByUser_Id(Long user_id, Pageable pageable);

    Optional<FavoriteProduct> findByUser_IdAndProduct_Id(Long user_id, Long product_id);
}
