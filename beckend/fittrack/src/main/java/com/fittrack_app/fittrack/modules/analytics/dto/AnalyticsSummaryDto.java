package com.fittrack_app.fittrack.modules.analytics.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@Builder
public class AnalyticsSummaryDto {
    private LocalDate startDate;
    private LocalDate endDate;

    private Double totalCaloriesConsumed;
    private Double averageDailyCalories;
    private Double totalProteinGrams;
    private Double totalFatGrams;
    private Double totalCarbsGrams;

    private Long totalWaterMl;

    private Long totalActivities;
    private Long totalActivityMinutes;
    private Double totalCaloriesBurned;

    private Double startingWeightKg;
    private Double endingWeightKg;
    private Double weightChangeKg;

}
