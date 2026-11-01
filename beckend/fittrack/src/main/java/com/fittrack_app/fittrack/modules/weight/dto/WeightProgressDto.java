package com.fittrack_app.fittrack.modules.weight.dto;

import com.fittrack_app.fittrack.modules.goals.enums.GoalType;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public class WeightProgressDto {
    private GoalType goalType;

    private Double startingWeightKg;

    private Double currentWeightKg;

    private Double targetWeightKg;

    private Double weightChangeKg;

    private Double remainingKg;

    private Double progressPercent;

    private LocalDate firstMeasurementDate;

    private LocalDate lastMeasurementDate;
}
