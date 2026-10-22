package com.fittrack_app.fittrack.modules.goals.repository;

import com.fittrack_app.fittrack.modules.goals.entity.UserGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserGoalRepository extends JpaRepository<UserGoal, Long > {
    Optional<UserGoal> findByUser_Id(Long userId);

    boolean existsByUser_Id(Long userId);
}
