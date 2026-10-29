package com.fittrack_app.fittrack.modules.activity.dto;

import com.fittrack_app.fittrack.modules.activity.enums.ActivityType;
import lombok.Data;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@Data
public class ActivityRequestDto {

    @NotNull(message = "Activity type is required")
    private ActivityType activityType;

    @NotNull(message = "Entry date is required")
    private LocalDate entryDate;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be greater than zero")
    private Integer durationMinutes;

    @NotNull(message = "Calories burned is required")
    @DecimalMin(
            value = "0.0",
            message = "Calories burned cannot be negative"
    )
    private Double caloriesBurned;
}
