package com.fittrack_app.fittrack.modules.activity.dto;

import com.fittrack_app.fittrack.modules.activity.enums.ActivityType;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Builder
public class ActivityResponseDto {
    private Long id;

    private ActivityType activityType;

    private LocalDate entryDate;

    private Integer durationMinutes;

    private Double caloriesBurned;

    private Instant createdAt;
}
