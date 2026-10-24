package com.fittrack_app.fittrack.modules.nutrition.repository;

import com.fittrack_app.fittrack.modules.nutrition.entity.FoodEntry;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
