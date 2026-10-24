package com.fittrack_app.fittrack.modules.nutrition.dto;

import com.fittrack_app.fittrack.modules.nutrition.enums.MealType;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Builder
public class FoodEntryResponseDto {


    private Long id;

    private Long productId;

    private String productName;

    private MealType mealType;

    private LocalDate entryDate;

    private Double quantityGrams;

    private Double calories;

    private Double proteinGrams;

    private Double fatGrams;

    private Double carbGrams;

    private Instant createdAt;
}
