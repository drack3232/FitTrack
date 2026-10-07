package com.fittrack_app.fittrack.modules.producte.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponseDto {
    private Long id;
    private String name;
    private String brand;
    private Double calories;
    private Double protein;
    private Double fat;
    private Double carbs;
}
