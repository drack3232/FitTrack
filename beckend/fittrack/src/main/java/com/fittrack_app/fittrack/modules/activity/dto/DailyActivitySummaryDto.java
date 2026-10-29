package com.fittrack_app.fittrack.modules.activity.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
@Getter
@Builder
public class DailyActivitySummaryDto {

    private LocalDate entryDate;

    private Long totalActivities;

    private Long totalDurationMinutes;

    private Double totalCaloriesBurned;

}
