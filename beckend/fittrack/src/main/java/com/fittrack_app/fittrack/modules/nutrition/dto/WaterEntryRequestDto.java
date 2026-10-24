package com.fittrack_app.fittrack.modules.nutrition.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class WaterEntryRequestDto {
    @NotNull(message = "Water amount is required")
    @Positive(message = "Water amount must be positive")
    private Integer amountMl;

    @NotNull(message = "Entry date is required")
    private LocalDate entryDate;
}
