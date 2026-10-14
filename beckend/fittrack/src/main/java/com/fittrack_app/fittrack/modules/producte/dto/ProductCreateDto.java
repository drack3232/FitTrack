package com.fittrack_app.fittrack.modules.producte.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProductCreateDto {

    @NotBlank(message = "Name of product can`t be empty")
    private String name;

    private String brand;

    @NotNull(message = "Enter calories by 100g")
    @PositiveOrZero(message = "Calories can`t be negative")
    private Double calories;

    @NotNull(message = "Enter quantity protein by 100g")
    @PositiveOrZero(message = "Protein can`t be negative")
    @Max(value = 100)
    private Double protein;
    @NotNull(message = "Enter quantity fat by 100g")
    @PositiveOrZero(message = "Fat can`t be negative")
    @Max(value = 100)
    private Double fat;

    @NotNull(message = "Enter quantity carbs by 100g")
    @PositiveOrZero(message = "Carbs can`t be negative ")
    @Max(value = 100)
    private Double carbs;

    @Pattern(
            regexp = "(?:\\d{8}|\\d{12,14})",
            message = "Barcode must contain 8, 12, 13 or 14 digits"
    )
    private String barcode;

}
