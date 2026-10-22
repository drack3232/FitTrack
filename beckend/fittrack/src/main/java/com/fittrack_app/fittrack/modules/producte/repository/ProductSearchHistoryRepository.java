package com.fittrack_app.fittrack.modules.producte.repository;

import com.fittrack_app.fittrack.modules.producte.entity.ProductSearchHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductSearchHistoryRepository extends JpaRepository<ProductSearchHistory, Long > {
    Optional<ProductSearchHistory> findByUser_IdAndNormalizedQuery(
            Long userId,
            String normalizedQuery
    );

    Page<ProductSearchHistory> findByUser_Id(
            Long userId,
            Pageable pageable
    );

    Optional<ProductSearchHistory> findByIdAndUser_Id(
            Long id,
            Long userId
    );

    @Modifying
    @Query("""
            DELETE FROM ProductSearchHistory h
            WHERE h.user.id = :userId
            """)
    int deleteAllForUser(@Param("userId") Long userId);
}
