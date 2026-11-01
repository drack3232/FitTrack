package com.fittrack_app.fittrack.modules.weight.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class WeightEntryRequestDto {
    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be greater than zero")
    private Double weightKg;

    @NotNull(message = "Entry date is required")
    private LocalDate entryDate;
}
