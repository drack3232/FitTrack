package com.fittrack_app.fittrack.modules.nutrition.repository;

import com.fittrack_app.fittrack.modules.nutrition.dto.FoodDailyAggregate;
import com.fittrack_app.fittrack.modules.nutrition.entity.FoodEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Repository
public interface FoodEntryRepository extends JpaRepository<FoodEntry, Long> {

    List<FoodEntry> findByUser_IdAndEntryDateOrderByCreatedAtAsc(
            Long userId,
            LocalDate entryDate
    );

    Optional<FoodEntry>findByIdAndUser_Id  (
            Long id,
            Long userId
    );


    List<FoodEntry> findByUser_IdAndEntryDateBetweenOrderByEntryDateAscCreatedAtAsc(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );
    @Query("""
    SELECT
        f.entryDate AS entryDate,
        SUM(f.calories) AS caloriesConsumed,
        SUM(f.proteinGrams) AS proteinGrams,
        SUM(f.fatGrams) AS fatGrams,
        SUM(f.carbGrams) AS carbsGrams
    FROM FoodEntry f
    WHERE f.user.id = :userId
      AND f.entryDate BETWEEN :startDate AND :endDate
    GROUP BY f.entryDate
    ORDER BY f.entryDate
    """)
    List<FoodDailyAggregate> findDailyNutritionAggregates(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
