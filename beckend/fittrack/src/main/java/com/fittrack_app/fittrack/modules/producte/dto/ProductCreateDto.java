package com.fittrack_app.fittrack.modules.producte.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
    private Double protein;
    @NotNull(message = "Enter quantity fat by 100g")
    @PositiveOrZero(message = "Fat can`t be negative")
    private Double fat;

    @NotNull(message = "Enter quantity carbs by 100g")
    @PositiveOrZero(message = "Carbs can`t be negative ")
    private Double carbs;

}
