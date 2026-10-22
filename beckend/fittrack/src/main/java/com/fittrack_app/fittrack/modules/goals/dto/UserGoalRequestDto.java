package com.fittrack_app.fittrack.modules.goals.dto;

import com.fittrack_app.fittrack.modules.goals.enums.ActivityLevel;
import com.fittrack_app.fittrack.modules.goals.enums.GoalType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UserGoalRequestDto {
    @NotNull(message = "Goal type is required")
    private GoalType goalType;

    @NotNull(message = "Activity level is required")
    private ActivityLevel activityLevel;

    @Positive(message = "Target weight must be positive")
    @DecimalMax(
            value = "500.0",
            message = "Target weight cannot exceed 500 kg"
    )
    private Double targetWeight;
}
