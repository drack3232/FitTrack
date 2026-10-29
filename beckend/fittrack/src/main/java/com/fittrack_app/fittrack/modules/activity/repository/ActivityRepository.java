package com.fittrack_app.fittrack.modules.activity.repository;

import com.fittrack_app.fittrack.modules.activity.entity.ActivityEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<ActivityEntry, Long> {
    List<ActivityEntry> findByUser_IdAndEntryDateOrderByCreatedAtAsc(Long userId, LocalDate entryDate);
    Optional<ActivityEntry> findByIdAndUser_Id(Long id, Long userId);
}
