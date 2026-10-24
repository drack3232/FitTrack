package com.fittrack_app.fittrack.modules.nutrition.dto;

import com.fittrack_app.fittrack.modules.nutrition.enums.MealType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FoodEntryRequestDto {

    @NotNull(message = "Product ID is required")
    @Positive(message = "Product ID must be positive")
    private Long productId;

    @NotNull(message = "Meal type is required")
    private MealType mealType;

    @NotNull(message = "Entry date is required")
    private LocalDate entryDate;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Double quantityGrams;

}
