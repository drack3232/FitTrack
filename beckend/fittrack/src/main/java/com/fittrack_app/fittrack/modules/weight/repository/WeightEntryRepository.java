package com.fittrack_app.fittrack.modules.weight.repository;

import com.fittrack_app.fittrack.modules.weight.entity.WeightEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeightEntryRepository extends JpaRepository<WeightEntry, Long> {
    List<WeightEntry> findByUser_IdOrderByEntryDateDescCreatedAtDescIdDesc(
            Long userId
    );

    Optional<WeightEntry> findByIdAndUser_Id(
            Long id,
            Long userId
    );

    Optional<WeightEntry> findFirstByUser_IdOrderByEntryDateAscCreatedAtAscIdAsc(
            Long userId
    );

    Optional<WeightEntry> findFirstByUser_IdOrderByEntryDateDescCreatedAtDescIdDesc(
            Long userId
    );

    List<WeightEntry> findByUser_IdAndEntryDateBetweenOrderByEntryDateAscCreatedAtAscIdAsc(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );
}
