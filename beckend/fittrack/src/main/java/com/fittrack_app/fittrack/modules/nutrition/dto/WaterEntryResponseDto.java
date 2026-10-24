package com.fittrack_app.fittrack.modules.nutrition.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Builder
public class WaterEntryResponseDto {
    private Long id;

    private Integer amountMl;

    private LocalDate entryDate;

    private Instant createdAt;
}
