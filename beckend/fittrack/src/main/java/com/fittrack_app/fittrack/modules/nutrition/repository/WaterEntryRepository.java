package com.fittrack_app.fittrack.modules.nutrition.repository;

import com.fittrack_app.fittrack.modules.nutrition.entity.WaterEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WaterEntryRepository extends JpaRepository<WaterEntry, Long> {

    Optional<WaterEntry> findByIdAndUser_Id(Long id, Long userId );

    @Query("""
            SELECT COALESCE(SUM(w.amountMl), 0L)
            FROM WaterEntry w
            WHERE w.user.id = :userId
              AND w.entryDate = :entryDate
            """)
    Long sumWaterByUserAndDate(
            @Param("userId") Long userId,
            @Param("entryDate") LocalDate entryDate
    );

    List<WaterEntry> findByUser_IdAndEntryDateOrderByCreatedAtAsc(
            Long userId,
            LocalDate entryDate
    );
}
