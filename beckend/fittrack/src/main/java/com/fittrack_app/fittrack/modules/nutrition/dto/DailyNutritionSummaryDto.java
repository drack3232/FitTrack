package com.fittrack_app.fittrack.modules.nutrition.dto;

import lombok.Builder;

import lombok.Getter;

import java.time.LocalDate;
@Getter
@Builder
public class DailyNutritionSummaryDto {

    private LocalDate entryDate;

    private Double totalCalories;

    private Double totalProteinGrams;

    private Double totalFatGrams;

    private Double totalCarbGrams;

    private Long totalWaterMl;

    private Double dailyCalorieTarget;

    private Double remainingCalories;
}
