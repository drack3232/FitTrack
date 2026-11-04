package com.fittrack_app.fittrack.modules.analytics.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DailyAnalyticsDto {
    private LocalDate date;


    private Double caloriesConsumed;
    private Double proteinGrams;
    private Double fatGrams;
    private Double carbsGrams;

    private Long waterMl;

    private Long activityCount;
    private Long activityMinutes;
    private Double caloriesBurned;

    private Double weightKg;

}
