package com.fittrack_app.fittrack.modules.goals.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CalorieCalculationResponseDto {
    private Double bmi;

    private Double bmr;

    private Double tdee;

    private Double dailyCalorieTarget;
}
