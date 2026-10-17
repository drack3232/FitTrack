package com.fittrack_app.fittrack.modules.producte.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProductUpdateDto {
    @Size(max = 255)
    @Pattern(
            regexp = ".*\\S.*",
            message = "Product name cannot be blank"
    )
    private String name;

    @Size(max = 255)
    private String brand;

    @PositiveOrZero
    private Double calories;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private Double protein;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private Double fat;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private Double carbs;

    @Pattern(
            regexp = "(?:\\d{8}|\\d{12,14})",
            message = "Invalid barcode format"
    )
    private String barcode;
}
