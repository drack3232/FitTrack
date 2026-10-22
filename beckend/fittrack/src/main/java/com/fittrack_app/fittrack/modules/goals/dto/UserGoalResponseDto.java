package com.fittrack_app.fittrack.modules.goals.dto;

import com.fittrack_app.fittrack.modules.goals.enums.ActivityLevel;
import com.fittrack_app.fittrack.modules.goals.enums.GoalType;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Builder
@Getter
public class UserGoalResponseDto {

    private Long id;

    private GoalType goalType;

    private ActivityLevel activityLevel;

    private Double targetWeight;

    private Instant createdAt;

    private Instant updatedAt;
}
