package com.fittrack_app.fittrack.modules.weight.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Builder
public class WeightEntryResponseDto {
    private Long id;

    private Double weightKg;

    private LocalDate entryDate;

    private Instant createdAt;

    private Instant updatedAt;
}
